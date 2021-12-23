package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.api.order.CancelReasons
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.rating.OrderReview
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IOrderRepo {
    suspend fun fetchOrder(
        orderId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Order>>

    suspend fun fetchCancelReasons(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<CancelReasons>>

    suspend fun cancelOrder(
        orderId: String,
        vendorId: String,
        reason: String,
        comment: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Order>>

    fun fetchOrderList(
        filters: Array<String>?,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Order>>

    suspend fun postReview(
        orderId: String,
        orderReview: OrderReview,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<OrderReview>>
}