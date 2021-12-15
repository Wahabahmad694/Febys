package com.hexagram.febys.paginations

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.product.Trending
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class TrendingProductsPagingSource constructor(
    private val service: FebysBackendService,
    private val request: PagingListRequest,
    onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
) : ProductListingPagingSource(onProductListingResponse) {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response = service.fetchTrendingProductsByUnits(queryMap)) {
            is ApiResponse.ApiSuccessResponse -> {
                val trending = response.data!!.getResponse<Trending>()
                val trendingProductsResponse = ProductPagingListing(
                    trending.getAllProducts(), trending.pagingInfo, trending.totalRows
                )
                onProductListingResponse?.invoke(trendingProductsResponse)
                val (prevKey, nextKey) = getPagingKeys(trendingProductsResponse.pagingInfo)
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