package com.hexagram.febys.paginations

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse

class WishlistPagingSource constructor(
    private val service: FebysBackendService,
    private val authToken: String,
    private val request: PagingListRequest,
    onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
) : ProductListingPagingSource(onProductListingResponse) {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        request.pageNo = params.key ?: 1
        val queryMap = request.createQueryMap()
        return when (val response = service.fetchWishlist(authToken, queryMap)) {
            is ApiResponse.ApiSuccessResponse -> {
                val wishlist = response.data!!.getResponse<ProductPagingListing>()
                onProductListingResponse?.invoke(wishlist)
                val (prevKey, nextKey) = getPagingKeys(wishlist.pagingInfo)
                LoadResult.Page(wishlist.products, prevKey, nextKey)
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