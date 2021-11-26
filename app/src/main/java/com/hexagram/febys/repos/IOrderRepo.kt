package com.hexagram.febys.repos

import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IOrderRepo {
    suspend fun fetchOrders(
        filters: Array<String>? = null, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<List<Order>>>
}