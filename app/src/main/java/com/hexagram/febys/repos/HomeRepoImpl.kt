package com.hexagram.febys.repos

import com.hexagram.febys.R
import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.product.Trending
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.FebysWebCustomizationService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.SeasonalOffer
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(
    private val webCustomizationService: FebysWebCustomizationService,
    private val backendService: FebysBackendService
) : IHomeRepo {

    override suspend fun fetchAllUniqueCategories(dispatcher: CoroutineDispatcher): List<UniqueCategory> {
        val response = webCustomizationService.fetchAllUniqueCategories()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchAllBanner(dispatcher: CoroutineDispatcher): List<Banner> {
        val response = webCustomizationService.fetchAllBanner()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchTodayDeals(dispatcher: CoroutineDispatcher): List<Product> {
        val response = backendService.fetchTodayDeals(ProductListingRequest())
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher): List<FeaturedCategory> {
        val response = backendService.fetchFeaturedCategories()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher): List<SeasonalOffer> {
        val response = webCustomizationService.fetchAllSeasonalOffers()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchTrendingProductsByUnits(dispatcher: CoroutineDispatcher): List<Product> {
        val response = backendService.fetchTrendingProductsByUnits()
        return getListOfProductFromTrendings(response)
    }

    override suspend fun fetchTrendingProductsBySale(dispatcher: CoroutineDispatcher): List<Product> {
        val response = backendService.fetchTrendingProductsBySale()
        return getListOfProductFromTrendings(response)
    }

    private fun getListOfProductFromTrendings(response: ApiResponse<Trending>): MutableList<Product> {
        val list = mutableListOf<Product>()
        val topPerformers = (response as? ApiResponse.ApiSuccessResponse)?.data?.topPerformers
        topPerformers?.forEach {
            list.addAll(it.product)
        }
        return list
    }

    override suspend fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher): List<String> {
        return listOf(
            "res:///${R.drawable.ic_shirt}",
            "res:///${R.drawable.ic_shirt}"
        )
    }

    override suspend fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher): List<Product> {
        val response = backendService.fetchUnder100DollarsItems()
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }
}