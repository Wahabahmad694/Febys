package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.models.api.notification.RemoteNotification
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.response.NotificationListingResponse
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class NotificationListingPagingSource(
    private val service: FebysBackendService,
    private val authToken: String,
    private val request: PagingListRequest
) : BasePagingSource<Int, RemoteNotification>() {

    override fun getRefreshKey(state: PagingState<Int, RemoteNotification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RemoteNotification> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        val response =
            service.fetchNotifications(authToken, queryMap, request)
        return when (response) {
            is ApiResponse.ApiSuccessResponse -> {
                val orders = response.data!!.getResponse<NotificationListingResponse>()
                val (prevKey, nextKey) = getPagingKeys(orders.pagingInfo)
                LoadResult.Page(orders.notifications, prevKey, nextKey)
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