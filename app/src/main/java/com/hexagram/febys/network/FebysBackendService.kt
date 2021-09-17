package com.hexagram.febys.network

import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.requests.RequestPushCart
import com.hexagram.febys.network.requests.RequestToggleFav
import com.hexagram.febys.network.response.*
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

    @GET("v1/products")
    suspend fun searchProducts(
        @Query("search") query: String, @QueryMap req: Map<String, Int>
    ): ApiResponse<ResponseOfPagination>

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

    @GET("v1/wish-list/variant-ids")
    suspend fun fetchWishlistIds(
        @Header("Authorization") authToken: String
    ): ApiResponse<ResponseToggleFav>

    @GET("v1/cart")
    suspend fun fetchCart(
        @Header("Authorization") authToken: String
    ): ApiResponse<Cart>

    @POST("v1/cart")
    suspend fun pushCart(
        @Header("Authorization") authToken: String,
        @Body req: RequestPushCart
    ): ApiResponse<Cart>

    @GET("v1/users/vendors-listing")
    suspend fun fetchVendors(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/users/celebrities-listing")
    suspend fun fetchCelebrities(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("v1/consumers/followed-vendors")
    suspend fun fetchFollowingVendors(@Header("Authorization") authToken: String): ApiResponse<ResponseVendorListing>

    @GET("v1/consumers/followed-celebrities")
    suspend fun fetchFollowingCelebrities(@Header("Authorization") authToken: String): ApiResponse<ResponseVendorListing>
}