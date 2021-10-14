package com.hexagram.febys.repos

import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IPaymentMethodRepo {
    suspend fun fetchPaymentMethods(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<PaymentMethod>>>

    suspend fun setAsDefault(id: Int)
}