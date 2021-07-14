package com.hexagram.febys.repos

import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import kotlinx.coroutines.CoroutineDispatcher
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

    fun fetchTrendingProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>
}