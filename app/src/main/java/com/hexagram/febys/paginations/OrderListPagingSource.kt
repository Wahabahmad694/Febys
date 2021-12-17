package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.request.OrderListingRequest
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.response.OrderListingResponse
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class OrderListPagingSource constructor(
    private val service: FebysBackendService,
    private val authToken: String,
    private val request: PagingListRequest,
    private val orderListingRequest: OrderListingRequest
) :  BasePagingSource<Int, Order>() {

    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Order> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response =
            service.fetchOrderListing(authToken, queryMap, orderListingRequest)) {
            is ApiResponse.ApiSuccessResponse -> {
                val orders = response.data!!.getResponse<OrderListingResponse>()
                val (prevKey, nextKey) = getPagingKeys(orders.pagingInfo)
                LoadResult.Page(orders.orders, prevKey, nextKey)
            }
            is ApiResponse.ApiFailureResponse.Error -> {
                LoadResult.Error(Exception(response.message))
            }
            is ApiResponse.ApiFailureResponse.Exception -> {
                LoadResult.Error(Exception(response.exception))
            }
        }
    }
}