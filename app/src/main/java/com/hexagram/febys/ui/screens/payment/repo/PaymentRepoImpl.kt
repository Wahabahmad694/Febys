package com.hexagram.febys.ui.screens.payment.repo

import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.ui.screens.payment.service.PaymentService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentRepoImpl @Inject constructor(
    private val paymentService: PaymentService,
    private val pref: IPrefManger
) : IPaymentRepo {
    override fun fetchWallet(conversionCurrency: String?, dispatcher: CoroutineDispatcher) =
        flow<DataState<Wallet>> {
            emit(DataState.Loading())
            val authToken = pref.getAccessToken()
            val walletResponse = paymentService.fetchWallet(authToken)
            if (conversionCurrency != null && walletResponse is ApiResponse.ApiSuccessResponse) {
                if (!conversionCurrency.equals(walletResponse.data!!.wallet.currency, true)) {
                    val wallet = walletResponse.data.wallet
                    val conversionRequest =
                        mapOf("from" to conversionCurrency, "to" to wallet.currency)
                    val conversionResponse =
                        paymentService.fetchCurrencyConversionRate(authToken, conversionRequest)
                    if (conversionResponse is ApiResponse.ApiSuccessResponse) {
                        wallet.conversionCurrency = conversionCurrency
                        wallet.conversionRate = 1.div(conversionResponse.data!!.conversionRate)
                    }
                }
            }
            walletResponse
                .onSuccess { emit(DataState.Data(data!!.wallet)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)

    override fun doWalletPayment(
        paymentRequest: PaymentRequest, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Transaction>> {
        emit(DataState.Loading())
        val authToken = pref.getAccessToken()
        paymentService.doWalletPayment(authToken, paymentRequest)
            .onSuccess { emit(DataState.Data(data!!.transaction)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override fun doPayStackPayment(
        paymentRequest: PaymentRequest, dispatcher: CoroutineDispatcher
    ) = flow<DataState<PayStackTransactionRequest>> {
        emit(DataState.Loading())
        val authToken = pref.getAccessToken()
        paymentService.doPayStackPayment(authToken, paymentRequest)
            .onSuccess { emit(DataState.Data(data!!.transactionRequest)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override fun listenToPayStackVerification(
        reference: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Transaction>> {
        emit(DataState.Loading())
        val authToken = pref.getAccessToken()
        paymentService.listenToPayStackVerification(authToken, reference)
            .onSuccess { emit(DataState.Data(data!!.transaction)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override fun notifyPaypalPayment(
        orderId: String, purpose: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Transaction>> {
        emit(DataState.Loading())
        val authToken = pref.getAccessToken()
        val req = mapOf("order_id" to orderId, "purpose" to purpose)
        paymentService.notifyPaypalPayment(authToken, req)
            .onSuccess { emit(DataState.Data(data!!.transaction)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}