package com.hexagram.febys.paginations

import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.OldProduct
import com.hexagram.febys.network.response.ResponseProductListing

class WishlistPagingSource constructor(
    private val service: FebysBackendService,
    private val authToken: String,
    private val request: RequestOfPagination,
    onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
) : ProductListingPagingSource(onProductListingResponse) {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OldProduct> {
        request.pageNo = params.key ?: 1
        val req = mapOf("chunkSize" to request.chunkSize, "pageNo" to request.pageNo)
        return when (val response = service.fetchWishlist(authToken, req)) {
            is ApiResponse.ApiSuccessResponse -> {
                val wishlist = response.data!!.getResponse<ResponseProductListing>()
                onProductListingResponse?.invoke(wishlist)
                val (prevKey, nextKey) = getPagingKeys(wishlist.paginationInformation)
                LoadResult.Page(wishlist.oldProducts, prevKey, nextKey)
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