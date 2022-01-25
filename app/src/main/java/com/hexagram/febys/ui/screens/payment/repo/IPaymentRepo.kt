package com.hexagram.febys.ui.screens.payment.repo

import androidx.paging.PagingData
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.transaction.Transaction
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.payment.models.PayStackTransactionRequest
import com.hexagram.febys.ui.screens.payment.models.Wallet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IPaymentRepo {
    fun fetchWallet(
        conversionCurrency: String? = null,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Wallet>>

    fun doWalletPayment(
        paymentRequest: PaymentRequest,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Transaction>>

    fun doPayStackPayment(
        paymentRequest: PaymentRequest,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<PayStackTransactionRequest>>

    fun listenToPayStackVerification(
        reference: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Transaction>>

    fun notifyPaypalPayment(
        orderId: String, purpose: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Transaction>>

    fun fetchTransactions(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Transaction>>
}