package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing

class CategoryProductsListingPagingSource constructor(
    private val service: FebysBackendService,
    private val categoryId: Int,
    private val request: RequestOfPagination
) : BasePagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        request.pageNo = params.key ?: 1
        val req = mapOf("chunkSize" to request.chunkSize, "pageNo" to request.pageNo)
        return when (val response = service.fetchCategoryProducts(categoryId, req)) {
            is ApiResponse.ApiSuccessResponse -> {
                val categoryProductsResponse = response.data!!.getResponse<ResponseProductListing>()
                val (prevKey, nextKey) = getPagingKeys(categoryProductsResponse.paginationInformation)
                LoadResult.Page(categoryProductsResponse.products, prevKey, nextKey)
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