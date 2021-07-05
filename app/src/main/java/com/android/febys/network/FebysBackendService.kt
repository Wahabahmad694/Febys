package com.android.febys.network

import com.android.febys.network.adapter.ApiResponse
import com.android.febys.network.requests.RequestAllCategories
import com.android.febys.network.response.Category
import com.android.febys.network.response.Product
import com.android.febys.network.response.ResponseOfPagination
import com.android.febys.network.response.ResponseProduct
import com.google.gson.JsonObject
import retrofit2.http.*

interface FebysBackendService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, RequestAllCategories>): JsonObject

    @GET("v1/products/today-deals")
    suspend fun fetchTodayDeals(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/categories/featured")
    suspend fun fetchFeaturedCategories(): ApiResponse<List<Category>>

    @GET("v1/products/today-deals")
    suspend fun fetchTrendingProducts(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/products/under100")
    suspend fun fetchUnder100DollarsItems(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/products/{productId}")
    suspend fun fetchProduct(@Path("productId") productId: Int): ApiResponse<ResponseProduct>
}