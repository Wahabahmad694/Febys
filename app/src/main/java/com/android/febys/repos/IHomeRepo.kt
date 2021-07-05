package com.android.febys.repos

import com.android.febys.network.DataState
import com.android.febys.network.response.Product
import com.android.febys.network.response.Banner
import com.android.febys.network.response.UniqueCategory
import com.android.febys.network.response.Category
import com.android.febys.network.response.SeasonalOffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IHomeRepo {
    fun fetchAllUniqueCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<UniqueCategory>>>

    fun fetchAllBanner(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Banner>>>

    fun fetchTodayDeals(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Category>>>

    fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<SeasonalOffer>>>

    fun fetchTrendingProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>

    fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<String>>>

    fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Product>>>
}