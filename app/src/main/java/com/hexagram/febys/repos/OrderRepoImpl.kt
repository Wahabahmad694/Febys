package com.hexagram.febys.repos

import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.request.OrderListingRequest
import com.hexagram.febys.models.api.response.OrderListingResponse
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderRepoImpl @Inject constructor(
    private val pref: IPrefManger,
    private val backendService: FebysBackendService
) : IOrderRepo {
    override suspend fun fetchOrders(
        filters: Map<String, String>?, dispatcher: CoroutineDispatcher
    ) = flow<DataState<List<Order>>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow

        val filtersBody = OrderListingRequest(filters)

        backendService.fetchOrderListing(authToken, filtersBody)
            .onSuccess {
                val orderResponse = data!!.getResponse<OrderListingResponse>()
                emit(DataState.Data(orderResponse.orders))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }
}