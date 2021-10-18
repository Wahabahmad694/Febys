package com.hexagram.febys.network

import com.google.gson.JsonObject
import com.hexagram.febys.models.api.pagination.Pagination
import com.hexagram.febys.models.api.product.FavSkuIds
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Trending
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.models.api.response.WishlistResponse
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.requests.RequestPushCart
import com.hexagram.febys.network.response.Cart
import com.hexagram.febys.network.response.ResponseOfPagination
import com.hexagram.febys.network.response.ResponseProduct
import com.hexagram.febys.network.response.ResponseVendorListing
import retrofit2.http.*

interface FebysBackendService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, RequestOfPagination>): ApiResponse<ResponseOfPagination>

    @GET("v1/products/today-deals")
    suspend fun fetchTodayDeals(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @POST("v1/consumers/products/today-deals")
    suspend fun fetchTodayDeals(@Body request: ProductListingRequest): ApiResponse<Pagination>

    @GET("v1/categories/featured")
    suspend fun fetchFeaturedCategories(): ApiResponse<List<FeaturedCategory>>

    @GET("v1/categories/{categoryId}/products")
    suspend fun fetchCategoryProducts(
        @Path("categoryId") categoryId: Int, @QueryMap req: Map<String, Int>
    ): ApiResponse<ResponseOfPagination>

    @GET("v1/products/trending")
    suspend fun fetchTrendingProducts(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @GET("/v1/top-performers/products/units")
    suspend fun fetchTrendingProductsByUnits(
        @Query("start_date") startDate: String = "2001-10-04T14:59:17.238Z",
        @Query("end_date") endDate: String = "2022-12-04T14:59:17.238Z"
    ): ApiResponse<Trending>

    @GET("/v1/top-performers/products/sale")
    suspend fun fetchTrendingProductsBySale(
        @Query("start_date") startDate: String = "2001-10-04T14:59:17.238Z",
        @Query("end_date") endDate: String = "2022-12-04T14:59:17.238Z"
    ): ApiResponse<Trending>

    @GET("v1/products/under100")
    suspend fun fetchUnder100DollarsItems(@QueryMap req: Map<String, Int>): ApiResponse<ResponseOfPagination>

    @POST("v1/consumers/products/under100")
    suspend fun fetchUnder100DollarsItems(@Body req: JsonObject = JsonObject()): ApiResponse<Pagination>

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

    @POST("v1/consumers/wishlist")
    suspend fun addToWishList(
        @Header("Authorization") authToken: String, @Body req: FavSkuIds
    ): ApiResponse<WishlistResponse>

    @HTTP(method = "DELETE", path = "v1/consumers/wishlist", hasBody = true)
    suspend fun removeFromWishList(
        @Header("Authorization") authToken: String, @Body req: FavSkuIds
    ): ApiResponse<FavSkuIds>

    @GET("v1/consumers/wishlist")
    suspend fun fetchWishlistIds(
        @Header("Authorization") authToken: String
    ): ApiResponse<FavSkuIds>

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

    @POST("v1/consumers/follow-user")
    suspend fun followVendor(
        @Header("Authorization") authKey: String, @Body req: Map<String, Int>
    ): ApiResponse<Unit>

    @POST("v1/consumers/unFollow-user")
    suspend fun unFollowVendor(
        @Header("Authorization") authKey: String, @Body req: Map<String, Int>
    ): ApiResponse<Unit>
}