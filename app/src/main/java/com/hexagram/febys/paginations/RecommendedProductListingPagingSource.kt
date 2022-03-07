package com.hexagram.febys.paginations

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class RecommendedProductListingPagingSource constructor(
    private val productId: String,
    private val service: FebysBackendService,
    private val request: PagingListRequest,
    onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
) : ProductListingPagingSource(onProductListingResponse) {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response = service.fetchRecommendProducts(queryMap, request)) {
            is ApiResponse.ApiSuccessResponse -> {
                val todayDealsResponse = response.data!!.getResponse<ProductPagingListing>()
                onProductListingResponse?.invoke(todayDealsResponse)
                val (prevKey, nextKey) = getPagingKeys(todayDealsResponse.pagingInfo)
                LoadResult.Page(todayDealsResponse.products, prevKey, nextKey)
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