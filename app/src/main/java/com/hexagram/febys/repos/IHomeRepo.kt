package com.hexagram.febys.repos

import com.hexagram.febys.network.response.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface IHomeRepo {
    suspend fun fetchAllUniqueCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<UniqueCategory>

    suspend fun fetchAllBanner(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Banner>

    suspend fun fetchTodayDeals(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Category>

    suspend fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<SeasonalOffer>

    suspend fun fetchTrendingProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<String>

    suspend fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>
}