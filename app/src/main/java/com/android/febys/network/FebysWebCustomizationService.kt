package com.android.febys.network

import com.android.febys.network.adapter.ApiResponse
import com.android.febys.network.response.Banner
import com.android.febys.network.response.SeasonalOffer
import com.android.febys.network.response.UniqueCategory
import retrofit2.http.GET

interface FebysWebCustomizationService {

    @GET("v1/unique-category/mobile")
    suspend fun fetchAllUniqueCategories(): ApiResponse<List<UniqueCategory>>

    @GET("v1/banner/mobile")
    suspend fun fetchAllBanner(): ApiResponse<List<Banner>>

    @GET("v1/seasonal-offer/mobile")
    suspend fun fetchAllSeasonalOffers(): ApiResponse<List<SeasonalOffer>>
}