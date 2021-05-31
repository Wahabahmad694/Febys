package com.android.febys.repos.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.android.febys.models.requests.RequestAllCategories
import com.android.febys.models.responses.Category
import com.android.febys.models.responses.ResponseAllCategories
import com.android.febys.network.FebysService
import com.google.gson.Gson

class CategoryPagingSource constructor(
    private val service: FebysService,
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