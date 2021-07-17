package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun fetchProductDetail(
        productId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Product>>

    fun fetchWishList(
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<List<Product>>>

    fun fetchTodayDealsListing(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Product>>

    fun fetchTrendingProductsListing(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Product>>

    fun fetchUnder100DollarsItemsListing(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Product>>

    fun fetchCategoryProductsListing(
        categoryId: Int,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>>
}