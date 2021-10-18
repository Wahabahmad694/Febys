package com.hexagram.febys.paginations

import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.OldProduct
import com.hexagram.febys.network.response.ResponseProductListing

class CategoryProductsListingPagingSource constructor(
    private val categoryId: Int,
    private val service: FebysBackendService,
    private val request: RequestOfPagination,
    onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
) : ProductListingPagingSource(onProductListingResponse) {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OldProduct> {
        request.pageNo = params.key ?: 1
        val req = mapOf("chunkSize" to request.chunkSize, "pageNo" to request.pageNo)
        return when (val response = service.fetchCategoryProducts(categoryId, req)) {
            is ApiResponse.ApiSuccessResponse -> {
                val categoryProductsResponse = response.data!!.getResponse<ResponseProductListing>()
                onProductListingResponse?.invoke(categoryProductsResponse)
                val (prevKey, nextKey) = getPagingKeys(categoryProductsResponse.paginationInformation)
                LoadResult.Page(categoryProductsResponse.oldProducts, prevKey, nextKey)
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