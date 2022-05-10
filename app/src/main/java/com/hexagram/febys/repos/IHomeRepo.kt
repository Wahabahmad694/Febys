package com.hexagram.febys.repos

import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.view.VendorListing
import com.hexagram.febys.network.response.SeasonalOffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface IHomeRepo {
    suspend fun fetchAllUniqueCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<UniqueCategory>

    suspend fun fetchAllBanner(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Banner>

    suspend fun fetchTodayDeals(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<FeaturedCategory>

    suspend fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<SeasonalOffer>

    suspend fun fetchTrendingProductsByUnits(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchTrendingProductsBySale(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchTrendingProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchEditorsPickItems(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun fetchFeaturedStores(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<VendorListing.Vendor>
}