package com.hexagram.febys.network

import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.api.pagination.Pagination
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Trending
import com.hexagram.febys.models.api.request.AskQuestionRequest
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.response.ProductDetailResponse
import com.hexagram.febys.models.api.response.QuestionAnswersResponse
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.models.api.vendor.VendorPagingListing
import com.hexagram.febys.models.api.wishlist.FavSkuIds
import com.hexagram.febys.models.api.wishlist.WishlistSkuIds
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestPushCart
import com.hexagram.febys.network.response.ResponseOfPagination
import retrofit2.http.*

interface FebysBackendService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, PagingListRequest>): ApiResponse<Pagination>

    @POST("v1/consumers/products/today-deals")
    suspend fun fetchTodayDeals(
        @QueryMap queryMap: Map<String, String>, @Body request: PagingListRequest
    ): ApiResponse<Pagination>

    @GET("v1/categories/featured")
    suspend fun fetchFeaturedCategories(): ApiResponse<List<FeaturedCategory>>

    @POST("v1/consumers/products/category/{categoryId}")
    suspend fun fetchCategoryProducts(
        @Path("categoryId") categoryId: Int,
        @QueryMap queryMap: Map<String, String>,
        @Body request: PagingListRequest
    ): ApiResponse<Pagination>

    @GET("/v1/top-performers/products/units")
    suspend fun fetchTrendingProductsByUnits(
        @QueryMap queryMap: Map<String, String>
    ): ApiResponse<Trending>

    @GET("/v1/top-performers/products/sale")
    suspend fun fetchTrendingProductsBySale(
        @QueryMap queryMap: Map<String, String>
    ): ApiResponse<Trending>

    @POST("v1/consumers/products/under100")
    suspend fun fetchUnder100DollarsItems(
        @QueryMap queryMap: Map<String, String>, @Body request: PagingListRequest
    ): ApiResponse<Pagination>

    @POST("v1/consumers/products/vendor/{vendorId}")
    suspend fun fetchVendorProducts(
        @Path("vendorId") vendorId: String,
        @QueryMap queryMap: Map<String, String>,
        @Body request: PagingListRequest
    ): ApiResponse<Pagination>

    @GET("v1/products")
    suspend fun searchProducts(
        @QueryMap queryMap: Map<String, String>, @Body request: PagingListRequest
    ): ApiResponse<ResponseOfPagination>

    @GET("v1/products/{productId}")
    suspend fun fetchProduct(@Path("productId") productId: String): ApiResponse<ProductDetailResponse>

    @POST("v1/consumers/wishlist/list")
    suspend fun fetchWishlist(
        @Header("Authorization") authToken: String, @QueryMap queryMap: Map<String, String>
    ): ApiResponse<Pagination>

    @POST("v1/consumers/wishlist")
    suspend fun addToWishList(
        @Header("Authorization") authToken: String, @Body req: FavSkuIds
    ): ApiResponse<WishlistSkuIds>

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
    ): ApiResponse<CartResponse>

    @POST("v1/cart")
    suspend fun pushCart(
        @Header("Authorization") authToken: String,
        @Body req: RequestPushCart
    ): ApiResponse<CartResponse>

    @POST("v1/consumers/stores")
    suspend fun fetchVendors(@QueryMap req: Map<String, String>): ApiResponse<Pagination>

    @POST("v1/consumers/stores/recommendations")
    suspend fun fetchRecommendVendors(
        @Header("Authorization") authToken: String, @QueryMap req: Map<String, String>
    ): ApiResponse<Pagination>

    @POST("v1/consumers/celebs")
    suspend fun fetchCelebrities(@QueryMap req: Map<String, String>): ApiResponse<Pagination>

    @POST("v1/consumers/celebs/recommendations")
    suspend fun fetchRecommendCelebrities(
        @Header("Authorization") authToken: String, @QueryMap req: Map<String, String>
    ): ApiResponse<Pagination>

    @GET("v1/consumers/stores/following")
    suspend fun fetchFollowingVendors(@Header("Authorization") authToken: String): ApiResponse<VendorPagingListing>

    @GET("v1/consumers/celebs/following")
    suspend fun fetchFollowingCelebrities(@Header("Authorization") authToken: String): ApiResponse<VendorPagingListing>

    @GET("v1/consumers/vendor-detail/{vendorId}")
    suspend fun fetchVendorDetail(@Path("vendorId") vendorId: String): ApiResponse<Vendor>

    @POST("v1/consumers/follow/{vendorId}")
    suspend fun followVendor(
        @Header("Authorization") authKey: String, @Path("vendorId") vendorId: String
    ): ApiResponse<Unit>

    @POST("v1/consumers/un-follow/{vendorId}")
    suspend fun unFollowVendor(
        @Header("Authorization") authKey: String, @Path("vendorId") vendorId: String
    ): ApiResponse<Unit>

    @POST("v1/consumers/products/recommended")
    suspend fun fetchRecommendProducts(@Body request: PagingListRequest): ApiResponse<Pagination>

    @POST("v1/consumers/products/{productId}/similar")
    suspend fun fetchSimilarProducts(
        @Path("productId") productId: String, @Body request: PagingListRequest
    ): ApiResponse<Pagination>

    @POST("v1/products/{productId}/ask-question")
    suspend fun askQuestion(
        @Header("Authorization") authKey: String,
        @Path("productId") productId: String,
        @Body askQuestionRequest: AskQuestionRequest
    ): ApiResponse<QuestionAnswersResponse>
}