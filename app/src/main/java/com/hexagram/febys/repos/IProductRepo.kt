package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
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

    fun fetchTodayDeals(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchTodayDealsListing(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Product>>

    fun fetchTrendingProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchTrendingProductsListing(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Product>>

    fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>
}