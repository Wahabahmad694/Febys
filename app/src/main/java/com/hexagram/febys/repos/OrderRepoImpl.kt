package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.api.order.CancelReasons
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.rating.OrderReview
import com.hexagram.febys.models.api.request.OrderListingRequest
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.paginations.OrderListPagingSource
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderRepoImpl @Inject constructor(
    private val pref: IPrefManger,
    private val backendService: FebysBackendService
) : IOrderRepo {

    override fun fetchOrderList(
        filters: Array<String>?,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): Flow<PagingData<Order>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authToken = pref.getAccessToken()
            var filterMap: Map<String, Map<String, Array<String>>>? = null
            if (!filters.isNullOrEmpty()) {
                filterMap = mapOf("vendor_products.status" to mapOf("\$in" to filters))
            }
            val filtersBody = OrderListingRequest(filterMap)
            OrderListPagingSource(
                backendService, authToken, PagingListRequest(), filtersBody
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override suspend fun fetchOrder(
        orderId: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Order>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow

        backendService.fetchOrder(authToken, orderId)
            .onSuccess { emit(DataState.Data(data!!.order)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    override suspend fun fetchCancelReasons(dispatcher: CoroutineDispatcher) =
        flow<DataState<CancelReasons>> {
            backendService.fetchCancelReasons()
                .onSuccess { emit(DataState.Data(data!!.cancelReasons)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }

    override suspend fun cancelOrder(
        orderId: String,
        vendorId: String,
        reason: String,
        comment: String,
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<Order>> {
        val authToken = pref.getAccessToken()
        val reqBody = mapOf("reason" to reason, "comments" to comment)

        backendService.cancelOrder(authToken, orderId, vendorId, reqBody)
            .onSuccess { emit(DataState.Data(data!!.order)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    override suspend fun postReview(
        orderId: String,
        orderReview: OrderReview,
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<OrderReview>> = flow<DataState<OrderReview>> {
        val authToken = pref.getAccessToken()
        backendService.postOrderReview(authToken, orderId, orderReview)
            .onSuccess { emit(DataState.Data(data!!)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }
}