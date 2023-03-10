package com.hexagram.febys.network

import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.menu.StoreMenusResponse
import com.hexagram.febys.models.api.menu.StoreTemplate
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.SeasonalOffer
import retrofit2.http.GET
import retrofit2.http.Path

interface FebysWebCustomizationService {

    @GET("v1/unique-category/mobile")
    suspend fun fetchAllUniqueCategories(): ApiResponse<List<UniqueCategory>>

    @GET("v1/banner/mobile")
    suspend fun fetchAllBanner(): ApiResponse<List<Banner>>

    @GET("v1/seasonal-offer/mobile")
    suspend fun fetchAllSeasonalOffers(): ApiResponse<List<SeasonalOffer>>

    @GET("v1/menu")
    suspend fun fetchAllMenu(): ApiResponse<List<StoreMenusResponse>>

    @GET("v1/store-templates/{storeId}")
    suspend fun fetchStoreTemplate(@Path("storeId") storeId: String): ApiResponse<StoreTemplate>
}