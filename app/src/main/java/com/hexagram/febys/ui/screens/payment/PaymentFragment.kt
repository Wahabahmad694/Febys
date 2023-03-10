package com.hexagram.febys.ui.screens.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.braintreepayments.api.*
import com.hexagram.febys.R
import com.hexagram.febys.databinding.FragmentPaymentBinding
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.models.brainTree.BraintreeRequest
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabRequest
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.Programs
import com.hexagram.febys.ui.screens.payment.utils.PayStackWebViewClient
import com.hexagram.febys.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentFragment : BasePaymentFragment() {
    private lateinit var binding: FragmentPaymentBinding

    private val DROP_IN_REQUEST_CODE = 0
    private var braintreeDeviceData: String = ""

    private val args by navArgs<PaymentFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentViewModel.initPaymentRequest(
            args.paymentRequest
        )
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
        setObserver()
        getFeeSlabs()
    }

    private fun initUi() {
        val totalAmount =
            Price("", args.paymentRequest.amount, args.paymentRequest.currency).getFormattedPrice()

        binding.tvTotalAmount.text = totalAmount
    }

    private fun uiListeners() {
        binding.ivBack.setOnClickListener { goBack() }

        binding.btnCheckout.setOnClickListener {
            handlePayNowClick()
        }

        binding.containerSplit.btnSplitPay.setOnClickListener {
            paymentViewModel.isSplitMode = true
            hideSplitScreen()
            proceedWithWalletPayment()
        }

        binding.containerSplit.btnCancel.setOnClickListener { hideSplitScreen() }

        binding.containerWalletPayment.setOnClickListener {
            if (!paymentViewModel.isSplitMode) {
                paymentViewModel.paymentMethod = PaymentMethod.WALLET
                updateUi(binding.containerWalletPayment, binding.walletFilledTick)
                binding.tvTotalAmount.text =
                    "${args.paymentRequest.currency} ${args.paymentRequest.amount.toString().convertTwoDecimal()}"
            }
        }

        binding.containerMomoPayment.setOnClickListener {
            paymentViewModel.paymentMethod = PaymentMethod.PAY_STACK
            updateUi(binding.containerMomoPayment, binding.momoFilledTick)
            binding.tvTotalAmount.text = "${args.paymentRequest.currency} ${
                args.paymentRequest.amount.plus(paymentViewModel.transactionFeePayStack)
            }"
        }

        binding.containerPaypalPayment.setOnClickListener {
            paymentViewModel.paymentMethod = PaymentMethod.PAYPAL
            updateUi(binding.containerPaypalPayment, binding.paypalFilledTick)
            binding.tvTotalAmount.text = "${args.paymentRequest.currency} ${
                args.paymentRequest.amount.plus(paymentViewModel.transactionFeePaypal)
            }"
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
                    disableWallet()
                }
                is DataState.Data -> {
                    hideLoader()
                    if (paymentViewModel.isSplitMode) {
                        binding.tvWalletPrice.text =
                            paymentViewModel.amountPaidFromWallet!!.getFormattedPrice()
                        binding.tvWalletPaymentMsg.text = getString(R.string.taken_from_wallet)
                    } else {
                        paymentViewModel.isWalletEnable = it.data.amount > 0.0
                        if (!paymentViewModel.isWalletEnable) {
                            disableWallet()
                        } else {
                            binding.tvWalletPrice.text = it.data.getPrice().getFormattedPrice()
                            binding.tvWalletPaymentMsg.text = getString(R.string.available)
                        }
                    }
                }
            }
        }
    }

    private fun getFeeSlabs() {
        if (paymentViewModel.isSplitMode) {
            val remaingFeeSlab = FeeSlabRequest(
                args.paymentRequest.currency,
                paymentViewModel.getRemainingPriceForSplit().value.toString()
            )
            paymentViewModel.getFeeSlab(remaingFeeSlab)
        } else {
            val feeSlabRequest = FeeSlabRequest(
                currency = args.paymentRequest.currency,
                amount = args.paymentRequest.amount.toString()
            )
            paymentViewModel.getFeeSlab(feeSlabRequest)
        }

    }

    private fun setObserver() {
        paymentViewModel.braintreeTokenResponse.observe(viewLifecycleOwner) {
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
                    it.data.let { it1 ->
                        val token = it1.transaction.clientToken
                        Log.d("BRAIN_TREE", "setObserver: $token")
                        dataCollector(token)
                        if (paymentViewModel.isSplitMode) {
                            val remainingAmount =
                                (paymentViewModel.transactionFeePaypal + paymentViewModel.getRemainingPriceForSplit().value).convertTwoDecimalForPayement()
                                    .toDouble()
                            callBrainTree(
                                token,
                                remainingAmount.toString()
                            )
                        } else {
                            val actualAmount =
                                (paymentViewModel.transactionFeePaypal + args.paymentRequest.amount).convertTwoDecimalForPayement()
                                    .toDouble()
                            callBrainTree(
                                token,
                                actualAmount.toString()
                            )
                        }
                    }
                }
            }
        }

        paymentViewModel.feeSlabsResponse.observe(viewLifecycleOwner) {
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
                    Log.d("Slabs_Fee", "setObserver: ${it.data.programs}")
                    showFeeSlabs(it.data.programs)
                }
            }
        }

        paymentViewModel.braintreeTransaction.observe(viewLifecycleOwner) {
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
                    Log.d("BRAIN_TREE_TRANS", "setObserver: ${it.data}")
                    paymentViewModel.saveTransaction(it.data)
                    onAllDone()
                }
            }
        }
    }

    private fun dataCollector(token: String) {
        val braintreeClient = BraintreeClient(requireContext(), token)
        val dataCollector = DataCollector(braintreeClient)
        dataCollector.collectDeviceData(requireContext()) { deviceData, _ ->
            // send deviceData to your server to be included in verification or transaction requests
            deviceData?.let {
                braintreeDeviceData = it
            }
        }
    }

    private fun showFeeSlabs(slabs: List<Programs>) {
        getProcessingFee("BRAINTREE", slabs)?.let {
            paymentViewModel.transactionFeePaypal =
                slabs.firstOrNull { it.gateway == "BRAINTREE" }?.slab?.value!!
            binding.braintreeAmount.text =
                getString(R.string.processing_fee_will_be_charged, it)
            specificTextColorChange(
                binding.braintreeAmount.text.toString(),
                binding.braintreeAmount.text.toString().split("card").first().length,
                binding.braintreeAmount.text.toString().split("processing")
                    .first().length,
                binding.braintreeAmount
            )
        }
        getProcessingFee("PAYSTACK", slabs)?.let {
            paymentViewModel.transactionFeePayStack =
                slabs.firstOrNull { it.gateway == "PAYSTACK" }?.slab?.value!!
            binding.paystackAmount.text =
                getString(R.string.processing_fee_will_be_charged_for_paystack, it)
            specificTextColorChange(
                binding.paystackAmount.text.toString(),
                0,
                binding.paystackAmount.text.toString().split("processing")
                    .first().length,
                binding.paystackAmount
            )
        }
    }


    private fun getProcessingFee(type: String, slabs: List<Programs>?): String? {
        slabs?.find { it.gateway == type }
            ?.let {
                return when (it.slab.type) {
                    "BOTH" -> {
                        "${(it.slab.percentage)}% + ${it.slab.currency}${it.slab.fixed} "
                    }
                    "PERCENTAGE" -> {
                        "${it.slab.percentage}% "
                    }
                    else -> {
                        "${it.slab.currency}${it.slab.fixed} "
                    }
                }
            }
        return null
    }

    fun createBraintreeTransaction(nonce: String) {
        Log.d("PaymentFragment1234567", "onCreate: $nonce")
        if (paymentViewModel.isSplitMode) {
            val remainingAmount =
                (paymentViewModel.transactionFeePaypal + paymentViewModel.getRemainingPriceForSplit().value).convertTwoDecimalForPayement()
                    .toDouble()
            val requestRemainingAmount = BraintreeRequest(
                remainingAmount,
                getCurrency(),
                braintreeDeviceData,
                nonce,
                paymentViewModel.transactionFeePaypal,
                paymentViewModel.getRemainingPriceForSplit().value,
                getCurrency(),
                "PRODUCT_PURCHASE"
            )
            paymentViewModel.doBrainTreeTransaction(requestRemainingAmount)
        } else {
            val totalAmountAfterConverted =
                (paymentViewModel.transactionFeePaypal + args.paymentRequest.amount).convertTwoDecimalForPayement()
                    .toDouble()
            val request = BraintreeRequest(
                totalAmountAfterConverted,
                getCurrency(),
                braintreeDeviceData,
                nonce,
                paymentViewModel.transactionFeePaypal,
                args.paymentRequest.amount,
                getCurrency(),
                "PRODUCT_PURCHASE"
            )
            paymentViewModel.doBrainTreeTransaction(request)
        }
    }


    private fun callBrainTree(token: String, amount: String) {

        val dropInRequest = DropInRequest()
        dropInRequest.maskCardNumber = true
        dropInRequest.maskSecurityCode = true
        dropInRequest.isPayPalDisabled = !isPaypalSupported(getCurrency())
        dropInRequest.threeDSecureRequest = demoThreeDSecureRequest(amount)
        val dropInClient = DropInClient(requireContext(), token, dropInRequest)

        dropInClient.launchDropInForResult(requireActivity(), DROP_IN_REQUEST_CODE)
    }


    private inline fun <reified T : Enum<T>> currencySupported(currencyCode: String?): Boolean {
        return enumValues<T>().any { it.name == currencyCode }
    }

    private fun isPaypalSupported(currencyCode: String?): Boolean {
        return try {
            currencySupported<PaypalCurrency>(currencyCode)
        } catch (e: java.lang.Exception) {
            false
        }
    }

    private fun demoThreeDSecureRequest(amount: String): ThreeDSecureRequest {

        val threeDSecureRequest = ThreeDSecureRequest()
        threeDSecureRequest.amount = amount.toDouble().toString()
        threeDSecureRequest.versionRequested = ThreeDSecureRequest.VERSION_2
        return threeDSecureRequest

    }

    private fun disableWallet() {
        binding.tvDisableWalletPrice.text =
            Price("", 0.0, Utils.DEFAULT_CURRENCY).getFormattedPrice()
        binding.containerDisableWallet.setOnClickListener(null)
        binding.containerDisableWallet.show()
        binding.containerWalletPayment.invisible()
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

    override fun doBrainTreePayment() {
        paymentViewModel.getBraintreeToken()
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
        if (paymentViewModel.isWalletEnable) {
            markAsNotSelected(binding.containerWalletPayment, binding.walletFilledTick)
        }
        markAsNotSelected(binding.containerMomoPayment, binding.momoFilledTick)
        markAsNotSelected(binding.containerPaypalPayment, binding.paypalFilledTick)

        markAsSelected(selectedBackground, filledTick)

        binding.containerRemainingPayment.isVisible = paymentViewModel.isSplitMode
        if (paymentViewModel.isSplitMode) {
            markAsSelected(binding.containerWalletPayment, binding.walletFilledTick)
            binding.walletFilledTick.isVisible = false

            val remainingPrice = paymentViewModel.getRemainingPriceForSplit().getFormattedPrice()

            val remainingPriceMsg =
                getString(R.string.label_choose_payment_for_remaining, remainingPrice)
            binding.tvRemainingAmount.showHtml(remainingPriceMsg)

        }
    }

    override fun onPaypalNotSupported() {
        binding.containerPaypalPayment.isVisible = true
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

