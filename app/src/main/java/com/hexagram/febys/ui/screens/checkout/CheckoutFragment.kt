package com.hexagram.febys.ui.screens.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private val cartAdapter = CartAdapter(true)

    private var orderPrice: Price? = null

    private var voucher = ""

    private val handleVoucherResponse: (voucherResponse: DataState<Order>) -> Unit = {
        handleOrderInfoResponse(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            showChangeShippingAddressWarningDialog()
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (checkoutViewModel.getDefaultShippingAddress() == null) {
                showToast(getString(R.string.error_please_select_shipping_address))
            } else {
                doPayment()
            }
        }

        binding.containerApplyVoucher.btnApplyVoucher.setOnClickListener {
            if (isUserLoggedIn) {
                voucher = binding.containerApplyVoucher.etApplyVoucher.text.toString()
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
                checkoutViewModel.removeFromCart(cartDTO)
                fetchOrderInfo()
            }

            override fun openProductDetail(cartDTO: CartDTO) {
                val navigateToProductDetail =
                    NavGraphDirections.actionToProductDetail(cartDTO.productId, cartDTO.skuId)
                navigateTo(navigateToProductDetail)
            }
        }
    }

    private fun fetchOrderInfo() {
        checkoutViewModel.fetchOrderInfo(voucher, handleVoucherResponse)
    }

    private fun handleOrderInfoResponse(orderInfoResponse: DataState<Order>) {
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
                createOrderSummary(orderInfoResponse.data)
            }
        }
    }

    private fun setObserver() {
        checkoutViewModel.observeCart().observe(viewLifecycleOwner) {
            val sortedListForCart = checkoutViewModel.sortListForCart(it)
            cartAdapter.submitList(sortedListForCart)
            if (sortedListForCart.isNullOrEmpty()) goBack()
        }
    }

    private fun createOrderSummary(order: Order) {
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

    private fun doPayment() {
        if (orderPrice == null) return
        val paymentRequest = PaymentRequest(orderPrice!!.value, orderPrice!!.currency)
        checkoutViewModel.doPayment(paymentRequest) {
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
                    placeOrder(it.data)
                }
            }
        }
    }

    private fun placeOrder(transaction: Transaction) {
        val vendorMessages = cartAdapter.getVendorMessages()
        checkoutViewModel.placeOrder(transaction._id, voucher, vendorMessages) {
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
                    checkoutViewModel.clearCart()
                    gotoCheckoutSuccessScreen(it.data.orderId)
                }
            }
        }
    }

    private fun gotoCheckoutSuccessScreen(orderId: String) {
        val destination = CheckoutFragmentDirections
            .actionCheckoutFragmentToCheckoutSuccessFragment(orderId)
        navigateTo(destination)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoader()
    }
}