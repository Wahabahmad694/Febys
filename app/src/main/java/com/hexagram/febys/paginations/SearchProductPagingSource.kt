package com.hexagram.febys.paginations

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class SearchProductPagingSource constructor(
    private val service: FebysBackendService,
    private val request: PagingListRequest,
    onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
) : ProductListingPagingSource(onProductListingResponse) {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response = service.searchProducts(queryMap, request)) {
            is ApiResponse.ApiSuccessResponse -> {
                val searchProductResponse = response.data!!.getResponse<ProductPagingListing>()
                onProductListingResponse?.invoke(searchProductResponse)
                val (prevKey, nextKey) = getPagingKeys(searchProductResponse.pagingInfo)
                LoadResult.Page(searchProductResponse.products, prevKey, nextKey)
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