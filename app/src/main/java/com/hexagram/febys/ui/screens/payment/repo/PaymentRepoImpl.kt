package com.hexagram.febys.ui.screens.payment.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonObject
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.network.BrainTree
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.paginations.TransactionPagingSource
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.ui.screens.payment.models.brainTree.TokenResponse
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabRequest
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabsResponse
import com.hexagram.febys.ui.screens.payment.service.PaymentService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PaymentRepoImpl @Inject constructor(
    private val paymentService: PaymentService,
    private val brainTree: BrainTree,
    private val pref: IPrefManger
) : IPaymentRepo {
    override fun fetchWallet(conversionCurrency: String?, dispatcher: CoroutineDispatcher) =
        flow<DataState<Wallet>> {
            emit(DataState.Loading())
            val authToken = pref.getAccessToken()
            val walletResponse = paymentService.fetchWallet(authToken)
            if (
                conversionCurrency != null
                && walletResponse is ApiResponse.ApiSuccessResponse
                && walletResponse.data?.wallet?.isWalletCreated == true
            ) {
                if (!conversionCurrency.equals(walletResponse.data.wallet.currency, true)) {
                    val wallet = walletResponse.data.wallet
                    val conversionRequest =
                        mapOf("from" to conversionCurrency, "to" to wallet.currency!!)
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

    override fun fetchTransactions(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher
    ): Flow<PagingData<Transaction>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authToken = pref.getAccessToken()
            val pagingListRequest = getPagingListRequestForTransaction()
            TransactionPagingSource(paymentService, authToken, pagingListRequest)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override suspend fun getBraintreeToken(
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<TokenResponse>> {
        emit(DataState.Loading())
        brainTree.getBraintreeToken()
            .onSuccess { data?.let { emit(DataState.Data(it)) } }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun feeSlabs(
        dispatcher: CoroutineDispatcher,
        request: FeeSlabRequest
    ) = flow<DataState<FeeSlabsResponse>>
    {
        emit(DataState.Loading())
        brainTree.feeSlabs(
            request = request
        )
            .onSuccess { data?.let { emit(DataState.Data(it)) } }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)


    private fun getPagingListRequestForTransaction(): PagingListRequest {
        val pagingListRequest = PagingListRequest()

        pagingListRequest.sorter = PagingListRequest.createDateSorter()

        val filters = JsonObject()
        val status = JsonObject()
        status.addProperty("\$ne", "REFUND")
        filters.add("status", status)
        pagingListRequest.filters = filters

        return pagingListRequest
    }
}