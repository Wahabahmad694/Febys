package com.android.febys.repos

import com.android.febys.network.DataState
import com.android.febys.network.domain.models.Product
import com.android.febys.network.domain.models.UniqueCategory
import com.android.febys.network.response.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IHomeRepo {
    fun fetchUniqueCategory(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<UniqueCategory>>>

    fun fetchSliderImages(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<String>>>

    fun fetchTodayDeals(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Category>>>

    fun fetchFeaturedCategoryProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchTrendingProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<String>>>

    fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>
}