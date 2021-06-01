package com.android.febys.paginations

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.febys.network.requests.RequestAllCategories
import com.android.febys.network.response.Category
import com.android.febys.network.response.ResponseAllCategories
import com.android.febys.network.FebysBackendService
import com.google.gson.Gson

class CategoryPagingSource constructor(
    private val service: FebysBackendService,
    private val request: RequestAllCategories
) : PagingSource<Int, Category>() {
    override fun getRefreshKey(state: PagingState<Int, Category>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Category> {
        return try {
            request.pageNo = params.key ?: 1
            val req = mapOf("listing" to request)
            val response = service.fetchAllCategories(req)
            val responseAllCategories =
                Gson().fromJson(response["listing"], ResponseAllCategories::class.java)

            val nextPageNo = if (
                responseAllCategories.paginationInformation.pageNo <
                responseAllCategories.paginationInformation.totalPages
            ) {
                responseAllCategories.paginationInformation.pageNo + 1
            } else {
                null
            }

            val previousPageNo = if (
                responseAllCategories.paginationInformation.pageNo == 1
            ) {
                null
            } else {
                responseAllCategories.paginationInformation.pageNo - 1
            }

            LoadResult.Page(
                data = responseAllCategories.categories,
                prevKey = previousPageNo,
                nextKey = nextPageNo
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}