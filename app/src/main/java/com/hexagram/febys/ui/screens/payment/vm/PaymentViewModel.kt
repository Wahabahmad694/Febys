package com.hexagram.febys.ui.screens.payment.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.payment.methods.PaymentMethod
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.ui.screens.payment.models.brainTree.BraintreeRequest
import com.hexagram.febys.ui.screens.payment.models.brainTree.TokenResponse
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabRequest
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabsResponse
import com.hexagram.febys.ui.screens.payment.repo.IPaymentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepo: IPaymentRepo
) : BaseViewModel() {
    var isWalletEnable: Boolean = true
    var paymentMethod: PaymentMethod? = null
    lateinit var paymentRequest: PaymentRequest
    var transactionFeePaypal: Double = 0.0
    var transactionFeePayStack: Double = 0.0
    private lateinit var wallet: Wallet

    /**
     * for the single order payment
     */
    private val transactions = mutableListOf<Transaction>()

    var isSplitMode: Boolean = false
    val showSplitMethod
        get() = paymentMethod == PaymentMethod.WALLET && paymentRequest.amount > wallet.amount

    var listenPayStack = false

    var amountPaidFromWallet: Price? = null

    /**
     * history of all transactions
     */

    private var _braintreeTokenResponse = MutableLiveData<DataState<TokenResponse>>()
    var braintreeTokenResponse: LiveData<DataState<TokenResponse>> = _braintreeTokenResponse

    private var _braintreeTransaction = MutableLiveData<DataState<Transaction>>()
    var braintreeTransaction: LiveData<DataState<Transaction>> = _braintreeTransaction

    private var _feeSlabsResponse = MutableLiveData<DataState<FeeSlabsResponse>>()
    var feeSlabsResponse: LiveData<DataState<FeeSlabsResponse>> = _feeSlabsResponse

    val allTransactions: Flow<PagingData<Transaction>> =
        paymentRepo.fetchTransactions(viewModelScope)

    fun initPaymentRequest(paymentRequest: PaymentRequest) {
        this.paymentRequest = paymentRequest
        val feeSlabRequest = FeeSlabRequest(
            paymentRequest.currency,
            paymentRequest.amount.toString()
        )
        getFeeSlab(feeSlabRequest)
    }

    fun refreshWallet() =
        paymentRepo.fetchWallet(
            if (::paymentRequest.isInitialized) paymentRequest.currency else null
        ).onEach {
            if (it is DataState.Data) wallet = it.data
        }.asLiveData()


    fun doWalletPayment(): LiveData<DataState<Transaction>> {
        val paymentRequest =
            if (isSplitMode) this.paymentRequest.copy(amount = wallet.amount) else this.paymentRequest
        return paymentRepo.doWalletPayment(paymentRequest).onEach {
            if (it is DataState.Data) {
                amountPaidFromWallet = Price("", paymentRequest.amount, paymentRequest.currency)
                transactions.add(it.data)
                val remainingPriceForSplit = getRemainingPriceForSplit()
                val feeSlabRequest = FeeSlabRequest(
                    remainingPriceForSplit.currency,
                    remainingPriceForSplit.value.toString()
                )
                getFeeSlab(feeSlabRequest)
            }
        }.asLiveData()
    }

    fun getRemainingPriceForSplit(): Price {
        val remainingAmount = paymentRequest.amount - amountPaidFromWallet?.value!!
        return Price("", remainingAmount, paymentRequest.currency)
    }

    fun doPayStackPayment(): LiveData<DataState<PayStackTransactionRequest>> {
        val payStackRequest =
            if (isSplitMode) this.paymentRequest.copy(amount = getRemainingPriceForSplit().value) else this.paymentRequest
        val payStackReqWithTransFee = payStackRequest.copy(
            transactionFee = transactionFeePayStack,
            billingAmount = transactionFeePayStack + payStackRequest.amount,
            billingCurrency = this.paymentRequest.currency
        )

        return paymentRepo.doPayStackPayment(payStackReqWithTransFee).asLiveData()
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

    fun saveTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    fun getTransactions(): List<Transaction> = transactions


    fun getBraintreeToken() = viewModelScope.launch {
        _braintreeTokenResponse.postValue(DataState.Loading())
        paymentRepo.getBraintreeToken(Dispatchers.IO).collect {
            _braintreeTokenResponse.postValue(it)
        }
    }

    fun getFeeSlab(feeSlabRequest: FeeSlabRequest) = viewModelScope.launch {
        _feeSlabsResponse.postValue(DataState.Loading())
        paymentRepo.feeSlabs(Dispatchers.IO, feeSlabRequest).collect {
            _feeSlabsResponse.postValue(it)
        }
    }

    fun doBrainTreeTransaction(braintreeRequest: BraintreeRequest) = viewModelScope.launch {
        _braintreeTransaction.postValue(DataState.Loading())
        paymentRepo.braintreeTransaction(Dispatchers.IO, braintreeRequest).collect {
            _braintreeTransaction.postValue(it)
        }
    }
}