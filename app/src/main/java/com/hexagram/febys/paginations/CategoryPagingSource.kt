package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.category.CategoryPagingListing
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class CategoryPagingSource constructor(
    private val service: FebysBackendService,
    private val request: PagingListRequest
) : BasePagingSource<Int, Category>() {
    override fun getRefreshKey(state: PagingState<Int, Category>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Category> {
        request.pageNo = params.key ?: 1
        val req = mapOf("listing" to request)
        return when (val response = service.fetchAllCategories(req)) {
            is ApiResponse.ApiSuccessResponse -> {
                val allCategories = response.data!!.getResponse<CategoryPagingListing>()
                val (prevKey, nextKey) = getPagingKeys(allCategories.pagingInfo)
                LoadResult.Page(allCategories.categories, prevKey, nextKey)
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