package com.hexagram.febys.ui.screens.payment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.BuildConfig
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.databinding.FragmentPaymentBinding
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.vm.PaymentViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.hideLoader
import com.hexagram.febys.utils.showLoader
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : BaseFragment() {
    private lateinit var binding: FragmentPaymentBinding
    private val paymentViewModel by viewModels<PaymentViewModel>()
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
                    binding.tvWalletPrice.text = it.data.getPrice().getFormattedPrice()
                }
            }
        }
    }

    private fun handlePayNowClick() {
        when (paymentViewModel.paymentMethod) {
            PaymentMethod.WALLET -> {
                doWalletPayment()
            }
            PaymentMethod.PAYPAL -> {

            }
            PaymentMethod.PAY_STACK -> {
                doPayStackPayment()
            }
            null -> {
                showToast(getString(R.string.please_select_a_payment_method))
            }
        }
    }

    private fun doPayStackPayment() {
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

    private fun handlePayStackPaymentRequestResponse(transactionRequest: PayStackTransactionRequest) {
        openPayStackWebView(transactionRequest.authorizationUrl, transactionRequest.reference)
        paymentViewModel
            .listenToPayStackVerification(transactionRequest.reference)
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
                        handlePayStackPaymentSuccessResponse()
                    }
                }
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun openPayStackWebView(url: String, reference: String) {
        binding.webView.apply {
            isVisible = true
            settings.javaScriptEnabled = true
            webViewClient = PayStackWebViewClient(reference)
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true
            binding.webView.loadUrl(url)
        }
    }

    inner class PayStackWebViewClient(refId: String) : WebViewClient() {
        private val callbackUrl = BuildConfig.backendBaseUrl + "v1/payments/transaction/paystack/callback"
        private val redirectURL = "$callbackUrl?reference=$refId"
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url: Uri? = request?.url
            if (url?.host == callbackUrl) {
                return true
            } else if (url.toString() == "https://standard.paystack.co/close") {
                binding.webView.loadUrl(redirectURL)
                return false
            }

            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    private fun handlePayStackPaymentSuccessResponse() {
        paymentViewModel.paymentMethod = null
        binding.webView.isVisible = false
        onAllDone()
    }

    private fun doWalletPayment() {
        if (paymentViewModel.showSplitMethod) {
            showSplitScreen()
        } else {
            proceedWithWalletPayment()
        }
    }

    private fun showSplitScreen() {
        binding.containerSplit.root.isVisible = true
        binding.containerPayment.isVisible = false
    }

    private fun hideSplitScreen() {
        binding.containerSplit.root.isVisible = false
        binding.containerPayment.isVisible = true
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
                    showToast("payment success: ${it.data._id}")
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


    private fun onAllDone() {
        setFragmentResult(
            TRANSACTIONS,
            bundleOf(TRANSACTIONS to paymentViewModel.getTransactions())
        )
        goBack()
    }

    companion object {
        const val TRANSACTIONS = "reqKeyTransactions"
    }
}

