package com.hexagram.febys.ui.screens.order.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentOrderDetailBinding
import com.hexagram.febys.databinding.LayoutOrderSummaryProductBinding
import com.hexagram.febys.models.api.cart.VendorProducts
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.order.OrderViewModel
import com.hexagram.febys.ui.screens.order.cancel.CancelOrderBottomSheet
import com.hexagram.febys.ui.screens.order.review.AddEditReviewFragment
import com.hexagram.febys.utils.*
import com.hexagram.febys.utils.Utils.DateTime.FORMAT_MONTH_DATE_YEAR_HOUR_MIN
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private val args: OrderDetailFragmentArgs by navArgs()
    private val orderDetailVendorProductAdapter = OrderDetailVendorProductAdapter()

    private var orderPrice: Price? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderViewModel.fetchOrder(args.orderId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()
    }

    private fun initUi() {
        binding.rvVendorWithProducts.isNestedScrollingEnabled = false
        binding.rvVendorWithProducts.adapter = orderDetailVendorProductAdapter
        binding.rvVendorWithProducts.applySpaceItemDecoration(R.dimen._16sdp)
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        orderDetailVendorProductAdapter.onCancelOrderClick = { vendorId ->
            gotoCancelOrder(args.orderId, vendorId)
        }
        orderDetailVendorProductAdapter.onAddReviewClick = { vendorProducts ->
            gotoAddReview(vendorProducts)
        }

        orderDetailVendorProductAdapter.onItemClick = { vendorProducts ->
            gotoAddReview(vendorProducts)
        }

        setFragmentResultListener(CancelOrderBottomSheet.REQ_KEY_IS_ORDER_CANCELED) { _, bundle ->
            val isOrderCanceled =
                bundle.getBoolean(CancelOrderBottomSheet.REQ_KEY_IS_ORDER_CANCELED)
            if (isOrderCanceled) orderViewModel.fetchOrder(args.orderId)
        }

        setFragmentResultListener(AddEditReviewFragment.REQ_KEY_REFRESH) { _, bundle ->
            val refresh =
                bundle.getBoolean(AddEditReviewFragment.REQ_KEY_REFRESH, false)

            if (refresh) {
                orderViewModel.fetchOrder(args.orderId)
            }
        }
    }

    private fun gotoCancelOrder(orderId: String, vendorId: String) {
        val gotoCancelOrder = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToCancelOrderBottomSheet(orderId, vendorId)
        navigateTo(gotoCancelOrder)
    }

    private fun gotoAddReview(vendorProducts: VendorProducts) {
        val actionOrderDetailFragmentToAddEditReviewFragment = OrderDetailFragmentDirections
            .actionOrderDetailFragmentToAddEditReviewFragment(args.orderId, vendorProducts)
        navigateTo(actionOrderDetailFragmentToAddEditReviewFragment)
    }

    private fun setObserver() {
        orderViewModel.observeOrder.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    hideLoader()
                    updateUi(it.data)
                }
            }
        }

        orderViewModel.observeTimer.observe(viewLifecycleOwner) {
            val showTimer = it.isNullOrEmpty().not()
            orderDetailVendorProductAdapter.updateCancelable(showTimer)
            binding.containerTimer.isVisible = showTimer
            binding.tvTimer.text = it
        }
    }

    private fun updateUi(order: Order) = with(binding) {
        tvOrderId.text = order.orderId
        tvOrderDate.text =
            Utils.DateTime.formatDate(order.createdAt, FORMAT_MONTH_DATE_YEAR_HOUR_MIN)

        orderDetailVendorProductAdapter.submitList(order.vendorProducts, args.review)

        createOrderSummary(order)
    }

    private fun createOrderSummary(order: Order) {
        binding.containerOrderSummary.root.isVisible = !args.review
        binding.containerOrderSummary.containerOrderSummaryProducts.removeAllViews()

        val cartItems = order.toListOfCartDTO()

        updateOrderSummaryQuantity(cartItems.size)

        cartItems.forEach {
            addProductToOrderSummary(it.productName, it.quantity, it.price)
        }

        addProductToOrderSummary(getString(R.string.label_subtotal), 1, order.productsAmount, true)

        val shippingFee = Price("", 0.0, order.productsAmount.currency)
        addProductToOrderSummary(getString(R.string.label_shipping_fee), 1, shippingFee, true)
        addVatToOrderSummary(order.vatPercentage, order.productsAmount)

        if (order.voucher != null) {
            val voucherDiscount = order.voucher.discount ?: 0.0
            val voucherPrice = Price("", -voucherDiscount, order.productsAmount.currency)
            addProductToOrderSummary(
                getString(R.string.label_voucher_discount), 1, voucherPrice, true
            )
        }

        updateTotalAmount(order.billAmount)
    }

    private fun addVatToOrderSummary(vatPercentage: Double, total: Price) {
        val productSummary = LayoutOrderSummaryProductBinding.inflate(
            layoutInflater,
            binding.containerOrderSummary.containerOrderSummaryProducts,
            false
        )

        val vatLabel = getString(R.string.label_vat)
        val vatWithPercentage = "$vatLabel $vatPercentage%"
        productSummary.tvProductNameWithQuantity.text = vatWithPercentage

        val vatAmount =
            if (vatPercentage > 0) total.value.times(vatPercentage).div(100.0) else 0.0
        val vatPrice = Price("", vatAmount, total.currency)

        productSummary.tvTotalPrice.text = vatPrice.getFormattedPrice()
        binding.containerOrderSummary.containerOrderSummaryProducts.addView(productSummary.root)
    }

    private fun addProductToOrderSummary(
        productName: String, quantity: Int, price: Price, hideQuantity: Boolean = false
    ) {
        val productSummary = LayoutOrderSummaryProductBinding.inflate(
            layoutInflater,
            binding.containerOrderSummary.containerOrderSummaryProducts,
            false
        )

        val productNameWithQuantity = if (hideQuantity) productName else "$quantity x $productName"
        productSummary.tvProductNameWithQuantity.text = productNameWithQuantity

        productSummary.tvTotalPrice.text = price.getFormattedPrice(quantity)
        binding.containerOrderSummary.containerOrderSummaryProducts.addView(productSummary.root)
    }

    private fun updateOrderSummaryQuantity(quantity: Int) {
        val itemString =
            if (quantity > 1) getString(R.string.label_items) else getString(R.string.label_item)
        val summaryQuantityString =
            getString(R.string.label_order_summary_with_quantity) + " ($quantity " + itemString + ")"
        binding.containerOrderSummary.labelOrderSummary.text = summaryQuantityString
    }

    private fun updateTotalAmount(price: Price) {
        orderPrice = price
        val totalAmountAsString = price.getFormattedPrice()
        binding.containerOrderSummary.tvTotalPrice.text = totalAmountAsString
    }
}