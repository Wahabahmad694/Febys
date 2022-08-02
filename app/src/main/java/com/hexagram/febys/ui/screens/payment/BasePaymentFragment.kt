package com.hexagram.febys.ui.screens.payment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.hexagram.febys.R
import com.hexagram.febys.base.BaseFragment
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import com.hexagram.febys.ui.screens.payment.vm.PaymentViewModel
import com.hexagram.febys.utils.goBack
import com.hexagram.febys.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BasePaymentFragment : BaseFragment() {
    companion object {
        const val TRANSACTIONS = "reqKeyTransactions"
    }

    protected val paymentViewModel by viewModels<PaymentViewModel>()
    private val payStackSupportedCurrency = listOf("NGN", "GHS", "ZAR")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPaypalConfig()
        initPayStack()
    }

    private fun initPayStack() {
        if (getCurrency() !in payStackSupportedCurrency) onPayStackNotSupported()
    }

    private fun initPaypalConfig() {
        val currencyCode = getCurrency()
        val isCurrencySupported = isPaypalSupportedCurrency(currencyCode)
//        if (!isCurrencySupported) {
//            onPaypalNotSupported()
//            return
//        }
    }

    protected fun isPaypalSupportedCurrency(currencyCode: String) = try {
//        CurrencyCode.valueOf(currencyCode)
        true
    } catch (e: Exception) {
        false
    }

    protected fun handlePayNowClick() {
        when (paymentViewModel.paymentMethod) {
            PaymentMethod.WALLET -> {
                doWalletPayment()
            }
            PaymentMethod.PAYPAL -> {
                doPaypalPayment()

            }
            PaymentMethod.PAY_STACK -> {
                doPayStackPayment()
            }
            null -> {
                showToast(getString(R.string.please_select_a_payment_method))
            }
        }
    }

    protected fun onAllDone() {
        setFragmentResult(
            TRANSACTIONS, bundleOf(TRANSACTIONS to paymentViewModel.getTransactions())
        )
        goBack()
    }

    abstract fun doWalletPayment()

        abstract fun doPaypalPayment()
    abstract fun doPayStackPayment()
    abstract fun getCurrency(): String
    abstract fun onPaypalNotSupported()
    abstract fun onPayStackNotSupported()
}