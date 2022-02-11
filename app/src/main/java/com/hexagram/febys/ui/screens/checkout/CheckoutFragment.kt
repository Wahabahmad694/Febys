package com.hexagram.febys.ui.screens.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentCheckoutBinding
import com.hexagram.febys.databinding.LayoutOrderSummaryProductBinding
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.cart.CartAdapter
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.BasePaymentFragment
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private val cartAdapter = CartAdapter(true)

    private var orderPrice: Price? = null

    private var voucher = ""
    private var validVoucher = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListener()
        setObserver()

        fetchOrderInfo()
    }

    private fun initUi() {
        binding.rvCart.isNestedScrollingEnabled = false
        binding.rvCart.adapter = cartAdapter

        val shippingAddress = checkoutViewModel.getDefaultShippingAddress()
        updateShippingAddressUi(shippingAddress)

        updateFav()
    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.containerShippingAddress.setOnClickListener {
            if (checkoutViewModel.getDefaultShippingAddress() == null) gotoShippingAddress()
            else showChangeShippingAddressWarningDialog()
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (checkoutViewModel.getDefaultShippingAddress() == null) {
                showToast(getString(R.string.error_please_select_shipping_address))
            } else {
                if (!validVoucher) {
                    showInvalidVoucherDialog()
                    return@setOnClickListener
                }
                val paymentRequest = PaymentRequest(orderPrice!!.value, orderPrice!!.currency)
                val gotoPayment = NavGraphDirections
                    .toPaymentFragment(paymentRequest)
                navigateTo(gotoPayment)
            }
        }

        binding.containerApplyVoucher.btnApplyVoucher.setOnClickListener {
            voucher = binding.containerApplyVoucher.etApplyVoucher.text.toString()
            fetchOrderInfo()
        }

        binding.containerApplyVoucher.btnRemoveVoucher.setOnClickListener {
            val resId = R.drawable.ic_error
            val title = getString(R.string.label_remove_voucher_warning)
            val msg = getString(R.string.msg_for_remove_voucher)
            showWarningDialog(resId, title, msg) {
                voucher = ""
                fetchOrderInfo()
            }
        }

        cartAdapter.interaction = object : CartAdapter.Interaction {
            override fun updateCartItem(cartDTO: CartDTO) {
                checkoutViewModel.updateCartItem(cartDTO)
                fetchOrderInfo()
            }

            override fun toggleFavIfUserLoggedIn(skuId: String): Boolean {
                return isUserLoggedIn.also {
                    if (it) {
                        checkoutViewModel.toggleFav(skuId)
                    } else {
                        val navigateToLogin = NavGraphDirections.actionToLoginFragment()
                        navigateTo(navigateToLogin)
                    }
                }
            }

            override fun removeFromCart(cartDTO: CartDTO) {
                val resId = R.drawable.ic_warning
                val title = getString(R.string.label_delete_warning)
                val msg = getString(R.string.msg_for_delete_item_bag)
                showWarningDialog(resId, title, msg) {
                    checkoutViewModel.removeFromCart(cartDTO)
                    fetchOrderInfo()
                }
            }

            override fun openProductDetail(cartDTO: CartDTO) {
                val navigateToProductDetail =
                    NavGraphDirections.actionToProductDetail(cartDTO.productId, cartDTO.skuId)
                navigateTo(navigateToProductDetail)
            }
        }

        setFragmentResultListener(BasePaymentFragment.TRANSACTIONS) { _, bundle ->
            val transactions = bundle
                .getParcelableArrayList<Transaction>(BasePaymentFragment.TRANSACTIONS)?.toList()

            if (!transactions.isNullOrEmpty()) {
                showOrderPlacingUi()
                showLoader()
                checkoutViewModel.cancelAllRunningJobs()
                placeOrder(transactions)
            }
        }
    }

    private fun showInvalidVoucherDialog() {
        val resId = R.drawable.ic_error
        val title = getString(R.string.label_invalid_voucher)
        val msg = getString(R.string.msg_for_invalid_voucher)

        showWarningDialog(resId, title, msg) {
            voucher = ""
            fetchOrderInfo()
        }
    }

    private fun fetchOrderInfo() {
        checkoutViewModel
            .fetchOrderInfo(voucher)
            .observe(viewLifecycleOwner) { handleOrderInfoResponse(it) }
    }

    private fun handleOrderInfoResponse(orderInfoResponse: DataState<Order?>) {
        when (orderInfoResponse) {
            is DataState.Loading -> {
                showLoader()
            }
            is DataState.Error -> {
                hideLoader()
                voucher = ""
                ErrorDialog(orderInfoResponse).show(childFragmentManager, ErrorDialog.TAG)
            }
            is DataState.Data -> {
                hideLoader()
                if (orderInfoResponse.data == null) {
                    goBack()
                    return
                }
                updateVoucherField()
                createOrderSummary(orderInfoResponse.data)
            }
        }
    }

    private fun updateVoucherField() {
        binding.containerApplyVoucher.etApplyVoucher.setText(voucher)
        binding.containerApplyVoucher.etApplyVoucher.isEnabled = voucher.isEmpty()
        binding.containerApplyVoucher.btnApplyVoucher.isVisible = voucher.isEmpty()
        binding.containerApplyVoucher.btnRemoveVoucher.isVisible = voucher.isNotEmpty()
    }

    private fun setObserver() {
        checkoutViewModel.observeCart().observe(viewLifecycleOwner) {
            hideLoader()
            val sortedListForCart = checkoutViewModel.sortListForCart(it)
            cartAdapter.submitList(sortedListForCart)
        }
    }

    private fun createOrderSummary(order: Order) {
        binding.containerOrderSummary.containerOrderSummaryProducts.removeAllViews()

        val cartItems = order.toListOfCartDTO()

        var totalItems = 0
        cartItems.forEach {
            totalItems += it.quantity
            addProductToOrderSummary(it.productName, it.quantity, it.price)
        }
        updateOrderSummaryQuantity(totalItems)

        addProductToOrderSummary(getString(R.string.label_subtotal), 1, order.productsAmount, true)

        addProductToOrderSummary(getString(R.string.label_shipping_fee), 1, order.deliveryFee, true)
        addVatToOrderSummary(order.vatPercentage, order.productsAmount)

        if (order.voucher != null) {
            val voucherDiscount = order.voucher.discount ?: 0.0
            val voucherPrice = Price("", -voucherDiscount, order.productsAmount.currency)
            addProductToOrderSummary(
                getString(R.string.label_voucher_discount), 1, voucherPrice, true
            )
        }

        updateTotalAmount(order.billAmount)
        validVoucher = order.billAmount.value > (order.voucher?.amount ?: 0.0)
    }

    private fun addProductToOrderSummary(
        productName: String, quantity: Int, price: Price, hideQuantity: Boolean = false,
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

    private fun addVatToOrderSummary(vatPercentage: Double, total: Price) {
        val productSummary = LayoutOrderSummaryProductBinding.inflate(
            layoutInflater,
            binding.containerOrderSummary.containerOrderSummaryProducts,
            false
        )

        val vatLabel = getString(R.string.label_vat)
        val vatWithPercentage = "$vatLabel ($vatPercentage%)"
        productSummary.tvProductNameWithQuantity.text = vatWithPercentage

        val vatAmount =
            if (vatPercentage > 0) total.value.times(vatPercentage).div(100.0) else 0.0
        val vatPrice = Price("", vatAmount, total.currency)

        productSummary.tvTotalPrice.text = vatPrice.getFormattedPrice()
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
        binding.tvTotalAmount.text = totalAmountAsString
        binding.containerOrderSummary.tvTotalPrice.text = totalAmountAsString
    }

    private fun updateShippingAddressUi(shippingAddress: ShippingAddress?) {
        binding.tvShippingAddress.text =
            shippingAddress?.shippingDetail?.address?.fullAddress()
                ?: getString(R.string.msg_for_no_shipping_address)
    }

    private fun showChangeShippingAddressWarningDialog() {
        val resId = R.drawable.ic_shipping_address
        val title = getString(R.string.label_shipping_address)
        val msg = getString(R.string.msg_for_change_shipping_address)

        this.showWarningDialog(resId, title, msg) { gotoShippingAddress() }
    }

    private fun updateFav() {
        val fav = checkoutViewModel.getFav()
        cartAdapter.submitFav(fav)
    }

    private fun gotoShippingAddress() {
        val gotoShippingAddress =
            CheckoutFragmentDirections.actionCheckoutFragmentToShippingAddressFragment()
        navigateTo(gotoShippingAddress)
    }

    private fun placeOrder(transactions: List<Transaction>) {
        val vendorMessages = cartAdapter.getVendorMessages()
        checkoutViewModel
            .placeOrder(transactions, voucher, vendorMessages)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is DataState.Loading -> {
                        showOrderPlacingUi()
                        showLoader()
                    }
                    is DataState.Error -> {
                        hideOrderPlacingUi()
                        hideLoader()
                        ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                    }
                    is DataState.Data -> {
                        hideLoader()
                        if (it.data == null) return@observe
                        binding.tvPlacingOrder.isVisible = false
                        val toCheckoutSuccess = CheckoutFragmentDirections
                            .actionCheckoutFragmentToCheckoutSuccessFragment(it.data.orderId)
                        navigateTo(toCheckoutSuccess)
                    }
                }
            }
    }

    private fun showOrderPlacingUi() {
        binding.containerPlacingOrder.isVisible = true
    }

    private fun hideOrderPlacingUi() {
        binding.containerPlacingOrder.isVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoader()
    }
}