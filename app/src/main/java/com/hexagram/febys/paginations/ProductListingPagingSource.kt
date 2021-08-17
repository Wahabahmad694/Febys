package com.hexagram.febys.paginations

import androidx.paging.PagingState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing

abstract class ProductListingPagingSource(
    val onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
) : BasePagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}