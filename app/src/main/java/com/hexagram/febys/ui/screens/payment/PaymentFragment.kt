package com.hexagram.febys.ui.screens.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentPaymentBinding
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.utils.PayStackWebViewClient
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import com.hexagram.febys.utils.showToast
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : BasePaymentFragment() {
    private lateinit var binding: FragmentPaymentBinding
    private val args by navArgs<PaymentFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentViewModel.paymentRequest = args.paymentRequest
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        uiListeners()
        refreshWallet()
    }

    private fun initUi() {
        val totalAmount =
            Price("", args.paymentRequest.amount, args.paymentRequest.currency).getFormattedPrice()

        binding.tvTotalAmount.text = totalAmount
    }

    private fun uiListeners() {
        binding.btnCheckout.setOnClickListener {
            handlePayNowClick()
        }

        binding.containerSplit.btnSplitPay.setOnClickListener {
            paymentViewModel.isSplitMode = true
            hideSplitScreen()
            proceedWithWalletPayment()
        }

        binding.containerWalletPayment.setOnClickListener {
            if (!paymentViewModel.isSplitMode) {
                paymentViewModel.paymentMethod = PaymentMethod.WALLET
                updateUi(binding.containerWalletPayment, binding.walletFilledTick)
            }
        }

        binding.containerMomoPayment.setOnClickListener {
            paymentViewModel.paymentMethod = PaymentMethod.PAY_STACK
            updateUi(binding.containerMomoPayment, binding.momoFilledTick)
        }

        binding.containerPaypalPayment.setOnClickListener {
            paymentViewModel.paymentMethod = PaymentMethod.PAYPAL
            updateUi(binding.containerPaypalPayment, binding.paypalFilledTick)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) { handleBackPress() }
    }

    private fun handleBackPress(): Boolean {
        return if (binding.webView.isVisible) {
            binding.webView.isVisible = false
            paymentViewModel.listenPayStack = false
            true
        } else {
            goBack()
            false
        }
    }

    private fun refreshWallet() {
        paymentViewModel.refreshWallet().observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                    binding.containerWalletPayment.isVisible = false
                }
                is DataState.Data -> {
                    hideLoader()
                    if (paymentViewModel.isSplitMode) {
                        binding.tvWalletPrice.text =
                            paymentViewModel.amountPaidFromWallet!!.getFormattedPrice()
                        binding.tvWalletPaymentMsg.text = getString(R.string.taken_from_wallet)
                    } else {
                        binding.tvWalletPrice.text = it.data.getPrice().getFormattedPrice()
                        binding.tvWalletPaymentMsg.text = getString(R.string.available)
                    }
                }
            }
        }
    }

    override fun doWalletPayment() {
        if (paymentViewModel.showSplitMethod) {
            showSplitScreen()
        } else {
            proceedWithWalletPayment()
        }
    }

    private fun proceedWithWalletPayment() {
        paymentViewModel.doWalletPayment().observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {
                    showLoader()
                }
                is DataState.Error -> {
                    hideLoader()
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                    paymentViewModel.isSplitMode = false
                }
                is DataState.Data -> {
                    hideLoader()
                    refreshWallet()
                    handleWalletPaymentSuccessResponse()
                }
            }
        }
    }

    private fun handleWalletPaymentSuccessResponse() {
        paymentViewModel.paymentMethod = null
        if (paymentViewModel.isSplitMode) {
            updateUi(binding.containerWalletPayment, binding.walletFilledTick)
        } else {
            onAllDone()
        }
    }

    override fun doPaypalPayment() {
        val amount =
            if (paymentViewModel.isSplitMode) paymentViewModel.getRemainingPriceForSplit().value else args.paymentRequest.amount
        val createOrder = CreateOrder {
            val order = Order(
                intent = OrderIntent.CAPTURE,
                appContext = AppContext(userAction = UserAction.PAY_NOW),
                purchaseUnitList = listOf(
                    PurchaseUnit(
                        amount = Amount(
                            currencyCode = CurrencyCode.valueOf(args.paymentRequest.currency),
                            value = amount.toString()
                        )
                    )
                )
            )
            it.create(order)
        }

        val onApprove = OnApprove { approval ->
            approval.orderActions.capture { captureOrderResult ->
                when (captureOrderResult) {
                    is CaptureOrderResult.Success -> {
                        handlePaypalSuccessResponse(
                            captureOrderResult.orderResponse?.id ?: return@capture
                        )
                    }
                    is CaptureOrderResult.Error -> {
                        showToast(captureOrderResult.reason)
                    }
                }
            }
        }

        val onCancel = OnCancel {
            // do nothing
        }

        val onError = OnError { errorInfo ->
            showToast(errorInfo.reason)
        }

        PayPalCheckout.start(createOrder, onApprove, null, onCancel, onError)
    }

    private fun handlePaypalSuccessResponse(orderId: String) {
        paymentViewModel.notifyPapalPayment(orderId, args.paymentRequest.purpose)
            .observe(viewLifecycleOwner) {
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
                        onAllDone()
                    }
                }
            }
    }

    override fun doPayStackPayment() {
        paymentViewModel.doPayStackPayment().observe(viewLifecycleOwner) {
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
                    handlePayStackPaymentRequestResponse(it.data)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun handlePayStackPaymentRequestResponse(transactionRequest: PayStackTransactionRequest) {
        binding.webView.apply {
            isVisible = true
            settings.javaScriptEnabled = true
            webViewClient = PayStackWebViewClient(transactionRequest.reference)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
            binding.webView.loadUrl(transactionRequest.authorizationUrl)
        }

        paymentViewModel.listenToPayStackVerification(transactionRequest.reference)
            .observe(viewLifecycleOwner) {
                when (it) {
                    is DataState.Loading -> {
                        showLoader()
                    }
                    is DataState.Error -> {
                        hideLoader()
                        ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                        binding.webView.isVisible = false
                    }
                    is DataState.Data -> {
                        hideLoader()
                        binding.webView.isVisible = false
                        paymentViewModel.paymentMethod = null
                        onAllDone()
                    }
                }
            }
    }

    private fun updateUi(selectedBackground: View, filledTick: View) {
        markAsNotSelected(binding.containerWalletPayment, binding.walletFilledTick)
        markAsNotSelected(binding.containerMomoPayment, binding.momoFilledTick)
        markAsNotSelected(binding.containerPaypalPayment, binding.paypalFilledTick)

        markAsSelected(selectedBackground, filledTick)

        binding.containerRemainingPayment.isVisible = paymentViewModel.isSplitMode
        if (paymentViewModel.isSplitMode) {
            markAsSelected(binding.containerWalletPayment, binding.walletFilledTick)

            val remainingPrice = paymentViewModel.getRemainingPriceForSplit().getFormattedPrice()
            binding.tvRemainingAmount.text =
                getString(R.string.label_choose_payment_for_remaining, remainingPrice)
        }
    }

    override fun onPaypalNotSupported() {
        binding.containerPaypalPayment.isVisible = false
    }

    override fun onPayStackNotSupported() {
        binding.containerMomoPayment.isVisible = false
    }

    override fun getCurrency() = args.paymentRequest.currency

    private fun showSplitScreen() {
        binding.containerSplit.root.isVisible = true
        binding.containerPayment.isVisible = false
    }

    private fun hideSplitScreen() {
        binding.containerSplit.root.isVisible = false
        binding.containerPayment.isVisible = true
    }

    private fun markAsSelected(backgroundView: View, tickView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_red)
        tickView.isVisible = true
    }

    private fun markAsNotSelected(backgroundView: View, tickView: View) {
        backgroundView.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_grey)
        tickView.isVisible = false
    }
}

