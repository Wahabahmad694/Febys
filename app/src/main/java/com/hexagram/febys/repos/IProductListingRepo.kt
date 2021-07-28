package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductListingRepo : IProductRepo {
    fun fetchTodayDealsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>>

    fun fetchTrendingProductsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>>

    fun fetchUnder100DollarsItemsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>>

    fun fetchCategoryProductsListing(
        categoryId: Int,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>>

    fun searchProductListing(
        query: String,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchWishList(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>>
}