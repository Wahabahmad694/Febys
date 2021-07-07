package com.hexagram.febys.repos

import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.Banner
import com.hexagram.febys.network.response.UniqueCategory
import com.hexagram.febys.network.response.Category
import com.hexagram.febys.network.response.SeasonalOffer
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