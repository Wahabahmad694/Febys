package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing

class TrendingProductsPagingSource constructor(
    private val service: FebysBackendService,
    private val request: RequestOfPagination,
    onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
) : ProductListingPagingSource<Int, Product>(onProductListingResponse) {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        request.pageNo = params.key ?: 1
        val req = mapOf("chunkSize" to request.chunkSize, "pageNo" to request.pageNo)
        return when (val response = service.fetchTrendingProducts(req)) {
            is ApiResponse.ApiSuccessResponse -> {
                val trendingProductsResponse = response.data!!.getResponse<ResponseProductListing>()
                onProductListingResponse?.invoke(trendingProductsResponse)
                val (prevKey, nextKey) = getPagingKeys(trendingProductsResponse.paginationInformation)
                LoadResult.Page(trendingProductsResponse.products, prevKey, nextKey)
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