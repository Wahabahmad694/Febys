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
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.request.EstimateRequest
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.models.swoove.Estimate
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.cart.CartAdapter
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.BasePaymentFragment
import com.hexagram.febys.ui.screens.shippingType.ShippingMethodFragment
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : BaseFragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private val checkoutViewModel: CheckoutViewModel by viewModels()
    private val cartAdapter = CartAdapter(true)

    private var orderPrice: Price? = null

    private var order: Order? = null
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

        if (binding.tvShippingMethod.text.isEmpty()) {
            binding.tvShippingMethod.text = "Please Select shipping Method"
            binding.tvSeparator.hide()
        }

        updateFav()
    }

    private fun updateShippingMethod(order: Order?) {
        if (checkoutViewModel.estimate != null) {
            val estimate = order?.swooveEstimates?.responses?.estimates
            estimate?.forEachIndexed { index, mEstimate ->
                if (mEstimate.estimateTypeDetails.name == checkoutViewModel.estimate!!.estimateTypeDetails.name) {
                    mEstimate.selected = true
                    binding.tvShippingMethod.text = mEstimate.estimateTypeDetails.name
                    binding.tvShippingDetail.text = mEstimate.timeString
                    binding.tvShippingFee.text =
                        "${mEstimate.totalPricing.currency_code} ${mEstimate.totalPricing.value}"
                }
            }
        } else {
            val estimate = order?.swooveEstimates?.responses?.estimates
            estimate?.forEachIndexed { index, mEstimate ->
                if (mEstimate.estimateId == order.swooveEstimates.responses.optimalEstimate.estimateId) {
                    mEstimate.selected = true
                    binding.tvShippingMethod.text = mEstimate.estimateTypeDetails.name
                    binding.tvShippingDetail.text = mEstimate.timeString
                    binding.tvShippingFee.text =
                        "${mEstimate.totalPricing.currency_code} ${mEstimate.totalPricing.value}"
                    checkoutViewModel.estimate = mEstimate

                }
            }
        }

    }

    private fun uiListener() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.containerShippingAddress.setOnClickListener {
            if (checkoutViewModel.getDefaultShippingAddress() == null) gotoShippingAddress()
            else showChangeShippingAddressWarningDialog()
        }
        binding.containerCourier.setOnClickListener {
            order?.let { it1 -> gotoShippingType(it1) }
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (orderPrice == null) return@setOnClickListener
            if (checkoutViewModel.getDefaultShippingAddress() == null) {
                showErrorPopUp()
            } else {
                if (!validVoucher) {
                    showInvalidVoucherDialog()
                    return@setOnClickListener
                }
                val paymentRequest = PaymentRequest(
                    orderPrice!!.value,
                    orderPrice!!.currency,
                    "PRODUCT_PURCHASE",
                    null,
                    null,
                    orderPrice!!.currency
                )
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

        cartAdapter.gotoVendorDetail = { vendorID -> gotoVendorDetail(vendorID, false) }

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

    private fun showErrorPopUpShippingMethod() {
        val resId = R.drawable.ic_error
        val title = getString(R.string.label_sorry)
        val msg = getString(R.string.error_add_shipping_method)

        showInfoDialoge(resId, title, msg) {
            // do nothing
        }
    }

    private fun showErrorPopUp() {
        val resId = R.drawable.ic_error
        val title = getString(R.string.label_error)
        val msg = getString(R.string.error_add_shipping_address)

        showInfoDialoge(resId, title, msg) {
            // do nothing
        }
    }

    private fun showInvalidVoucherDialog() {
        val resId = R.drawable.ic_error
        val title = getString(R.string.label_invalid_voucher)
        val msg = getString(R.string.msg_for_invalid_voucher)

        showWarningDialog(resId, title, msg, false) {
            voucher = ""
            fetchOrderInfo()
        }
    }

    private fun gotoVendorDetail(vendorId: String, isFollow: Boolean) {
        val direction = NavGraphDirections.toVendorDetailFragment(vendorId, isFollow)
        navigateTo(direction)
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
                order = orderInfoResponse.data
                if (order == null) {
                    goBack()
                    return
                }
                updateVoucherField()
                updateShippingMethod(order)
                order!!.addToOrderSummary(binding.containerOrderSummary)
                updateTotalAmount(order!!.totalAmountAsString)
                validVoucher = order!!.productsAmount.value > (order!!.voucher?.amount ?: 0.0)
                if (!validVoucher) {
                    showInvalidVoucherDialog()
                }

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
            val sortedListForCart = checkoutViewModel.sortListForCart(it)
            cartAdapter.submitList(sortedListForCart)
        }


        setFragmentResultListener(ShippingMethodFragment.ESTIMATE) { _, bundle ->
            val estimate =
                bundle.getParcelable<Estimate>(ShippingMethodFragment.ESTIMATE)
            checkoutViewModel.estimate = estimate
        }
    }

    private fun updateTotalAmount(price: Price) {
        orderPrice = price
        val totalAmountAsString = price.getFormattedPrice()
        binding.tvTotalAmount.text = totalAmountAsString
    }

    private fun updateShippingAddressUi(shippingAddress: ShippingAddress?) {
        binding.tvShippingAddress.text =
            shippingAddress?.shippingDetail?.address?.singleLineAddress()
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

    private fun gotoShippingType(order: Order) {
        if (order.shippingAddress == null) {
            showErrorPopUpShippingMethod()
        } else {
            val gotoShippingType =
                CheckoutFragmentDirections.actionCheckoutFragmentToShippingTypeFragment(order)
            navigateTo(gotoShippingType)
        }

    }

    private fun placeOrder(transactions: List<Transaction>) {
        val vendorMessages = cartAdapter.getVendorMessages()
        val estimateRequest = checkoutViewModel.estimate?.let {
            EstimateRequest(
                estimate = it
            )
        }
        checkoutViewModel
            .placeOrder(transactions, voucher, vendorMessages, estimateRequest)
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