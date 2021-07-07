package com.hexagram.febys.network

import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.Banner
import com.hexagram.febys.network.response.SeasonalOffer
import com.hexagram.febys.network.response.UniqueCategory
import retrofit2.http.GET

interface FebysWebCustomizationService {

    @GET("v1/unique-category/mobile")
    suspend fun fetchAllUniqueCategories(): ApiResponse<List<UniqueCategory>>

    @GET("v1/banner/mobile")
    suspend fun fetchAllBanner(): ApiResponse<List<Banner>>

    @GET("v1/seasonal-offer/mobile")
    suspend fun fetchAllSeasonalOffers(): ApiResponse<List<SeasonalOffer>>
}