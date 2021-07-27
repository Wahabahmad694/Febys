package com.hexagram.febys.network

import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.requests.RequestToggleFav
import com.hexagram.febys.network.response.Category
import com.hexagram.febys.network.response.ResponseOfPagination
import com.hexagram.febys.network.response.ResponseProduct
import com.hexagram.febys.network.response.ResponseToggleFav
import retrofit2.http.*

interface FebysBackendService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, RequestOfPagination>): ApiResponse<ResponseOfPagination>

    @GET("v1/products/today-deals")
    suspend fun fetchTodayDeals(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/categories/featured")
    suspend fun fetchFeaturedCategories(): ApiResponse<List<Category>>

    @GET("v1/categories/{categoryId}/products")
    suspend fun fetchCategoryProducts(
        @Path("categoryId") categoryId: Int, @QueryMap req: Map<String, Int>
    ): ApiResponse<ResponseOfPagination>

    @GET("v1/products/trending")
    suspend fun fetchTrendingProducts(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/products/under100")
    suspend fun fetchUnder100DollarsItems(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/products/{productId}")
    suspend fun fetchProduct(@Path("productId") productId: Int): ApiResponse<ResponseProduct>

    @GET("v1/wish-list")
    suspend fun fetchWishlist(
        @Header("Authorization") authToken: String, @QueryMap req: Map<String, Int>
    ): ApiResponse<ResponseOfPagination>

    @POST("v1/wish-list")
    suspend fun addToWishList(
        @Header("Authorization") authToken: String, @Body req: RequestToggleFav
    ): ApiResponse<ResponseToggleFav>

    @HTTP(method = "DELETE", path = "v1/wish-list", hasBody = true)
    suspend fun removeFromWishList(
        @Header("Authorization") authToken: String, @Body req: RequestToggleFav
    ): ApiResponse<ResponseToggleFav>
}