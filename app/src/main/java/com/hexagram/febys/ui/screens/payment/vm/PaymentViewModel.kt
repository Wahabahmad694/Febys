package com.hexagram.febys.ui.screens.payment.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.ui.screens.payment.repo.IPaymentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepo: IPaymentRepo
) : BaseViewModel() {
    var paymentMethod: PaymentMethod? = null
    lateinit var paymentRequest: PaymentRequest
    private lateinit var wallet: Wallet

    private val transactions = mutableListOf<Transaction>()

    var isSplitMode: Boolean = false
    val showSplitMethod
        get() = paymentMethod == PaymentMethod.WALLET && paymentRequest.amount > wallet.amount

    var listenPayStack = false

    var amountPaidFromWallet = 0.0

    fun refreshWallet() = paymentRepo.fetchWallet(paymentRequest.currency).onEach {
        if (it is DataState.Data) wallet = it.data
    }.asLiveData()


    fun doWalletPayment(): LiveData<DataState<Transaction>> {
        val paymentRequest =
            if (isSplitMode) this.paymentRequest.copy(amount = wallet.amount) else this.paymentRequest
        return paymentRepo.doWalletPayment(paymentRequest).onEach {
            if (it is DataState.Data) {
                amountPaidFromWallet = paymentRequest.amount
                transactions.add(it.data)
            }
        }.asLiveData()
    }

    fun getRemainingPriceForSplit(): Price {
        val remainingAmount = paymentRequest.amount - amountPaidFromWallet
        return Price("", remainingAmount, paymentRequest.currency)
    }

    fun doPayStackPayment(): LiveData<DataState<PayStackTransactionRequest>> {
        val paymentRequest =
            if (isSplitMode) this.paymentRequest.copy(amount = getRemainingPriceForSplit().value) else this.paymentRequest
        return paymentRepo.doPayStackPayment(paymentRequest).asLiveData()
    }

    fun listenToPayStackVerification(reference: String) = channelFlow {
        listenPayStack = true
        while (listenPayStack) {
            paymentRepo.listenToPayStackVerification(reference).collectLatest {
                if (it is DataState.Data) {
                    listenPayStack = it.data.status == "REQUESTED"
                    if (it.data.status == "PENDING_CLAIM") {
                        transactions.add(it.data)
                        send(it)
                    } else if (it.data.status == "REJECTED") {
                        send(DataState.ApiError("Payment Failed"))
                    }
                } else if (it is DataState.Error) {
                    send(it)
                    listenPayStack = false
                }
            }
            delay(2000)
        }

    }.asLiveData()

    fun notifyPapalPayment(orderId: String, purpose: String) =
        paymentRepo.notifyPaypalPayment(orderId, purpose).onEach {
            if (it is DataState.Data) transactions.add(it.data)
        }.asLiveData()

    fun getTransactions(): List<Transaction> = transactions
}