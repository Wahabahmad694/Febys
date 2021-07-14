package com.hexagram.febys.repos

import com.hexagram.febys.R
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.FebysWebCustomizationService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.*
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
        val req = mapOf("chunkSize" to 10, "pageNo" to 1)
        val response = backendService.fetchTodayDeals(req)
        return (response as? ApiResponse.ApiSuccessResponse)?.data
            ?.getResponse<ResponseProductListing>()?.products ?: emptyList()
    }

    override suspend fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher): List<Category> {
        val response = backendService.fetchFeaturedCategories()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher): List<SeasonalOffer> {
        val response = webCustomizationService.fetchAllSeasonalOffers()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchTrendingProducts(dispatcher: CoroutineDispatcher): List<Product> {
        val req = mapOf("chunkSize" to 10, "pageNo" to 1)
        val response = backendService.fetchTrendingProducts(req)
        return (response as? ApiResponse.ApiSuccessResponse)?.data
            ?.getResponse<ResponseProductListing>()?.products ?: emptyList()
    }

    override suspend fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher): List<String> {
        return listOf(
            "res:///${R.drawable.ic_shirt}",
            "res:///${R.drawable.ic_shirt}"
        )
    }

    override suspend fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher): List<Product> {
        val req = mapOf("chunkSize" to 10, "pageNo" to 1)
        val response = backendService.fetchUnder100DollarsItems(req)
        return (response as? ApiResponse.ApiSuccessResponse)?.data
            ?.getResponse<ResponseProductListing>()?.products ?: emptyList()
    }
}