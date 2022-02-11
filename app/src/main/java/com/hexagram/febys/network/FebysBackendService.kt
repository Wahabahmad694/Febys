package com.hexagram.febys.network

import com.google.gson.JsonObject
import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.api.cities.PostCitiesResponse
import com.hexagram.febys.models.api.countries.CountryResponse
import com.hexagram.febys.models.api.febysPlusPackage.FebysPackageResponse
import com.hexagram.febys.models.api.filters.SearchFilterResponse
import com.hexagram.febys.models.api.pagination.Pagination
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.profile.Profile
import com.hexagram.febys.models.api.rating.OrderReview
import com.hexagram.febys.models.api.request.*
import com.hexagram.febys.models.api.response.*
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.shippingAddress.ShippingAddressResponse
import com.hexagram.febys.models.api.states.PostStatesResponse
import com.hexagram.febys.models.api.transaction.TransactionReq
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.models.api.vendor.VendorPagingListing
import com.hexagram.febys.models.api.vouchers.VoucherResponse
import com.hexagram.febys.models.api.wishlist.FavSkuIds
import com.hexagram.febys.models.api.wishlist.WishlistSkuIds
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestPushCart
import com.hexagram.febys.network.requests.RequestReturnOrder
import com.hexagram.febys.network.requests.RequestUpdateUser
import com.hexagram.febys.network.requests.ResponseUpdateUser
import com.hexagram.febys.network.response.ResponseOfPagination
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface FebysBackendService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, PagingListRequest>): ApiResponse<Pagination>

    @POST("v1/consumers/products/today-deals")
    suspend fun fetchTodayDeals(
        @QueryMap queryMap: Map<String, String>, @Body request: PagingListRequest
    ): ApiResponse<Pagination>

    @POST("v1/consumers/products/spacial/type")
    suspend fun fetchSpecialProducts(
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

    @GET("v1/top-performers/products/units")
    suspend fun fetchTrendingProductsByUnits(
        @QueryMap queryMap: Map<String, String>
    ): ApiResponse<Pagination>

    @GET("v1/top-performers/products/sale")
    suspend fun fetchTrendingProductsBySale(
        @QueryMap queryMap: Map<String, String>
    ): ApiResponse<Pagination>

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

    @POST("v1/consumers/products")
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

    @POST("v1/vendors/{vendorId}/endorsements")
    suspend fun fetchVendorEndorsements(@Path("vendorId") vendorId: String): ApiResponse<EndorsementResponse>

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

    @POST("v1/products/{productId}/ask-question")
    suspend fun replyQuestion(
        @Header("Authorization") authKey: String,
        @Path("productId") productId: String,
        @Body askQuestionRequest: ReplyQuestionRequest
    ): ApiResponse<QuestionAnswersResponse>

    @POST("v1/products/{productId}/threads/{threadId}/up-vote")
    suspend fun questionVoteUp(
        @Header("Authorization") authToken: String,
        @Path("productId") productId: String,
        @Path("threadId") threadId: String
    ): ApiResponse<QuestionAnswersResponse>

    @DELETE("v1/products/{productId}/threads/{threadId}/up-vote")
    suspend fun revokeQuestionVoteUp(
        @Header("Authorization") authToken: String,
        @Path("productId") productId: String,
        @Path("threadId") threadId: String
    ): ApiResponse<QuestionAnswersResponse>

    @POST("v1/products/{productId}/threads/{threadId}/down-vote")
    suspend fun questionVoteDown(
        @Header("Authorization") authToken: String,
        @Path("productId") productId: String,
        @Path("threadId") threadId: String
    ): ApiResponse<QuestionAnswersResponse>

    @DELETE("v1/products/{productId}/threads/{threadId}/down-vote")
    suspend fun revokeQuestionVoteDown(
        @Header("Authorization") authToken: String,
        @Path("productId") productId: String,
        @Path("threadId") threadId: String
    ): ApiResponse<QuestionAnswersResponse>

    @POST("v1/rating-review/{reviewId}/product/up-vote")
    suspend fun reviewVoteUp(
        @Header("Authorization") authToken: String,
        @Path("reviewId") reviewId: String
    ): ApiResponse<RatingAndReviewsResponse>

    @DELETE("v1/rating-review/{reviewId}/product/up-vote")
    suspend fun revokeReviewVoteUp(
        @Header("Authorization") authToken: String,
        @Path("reviewId") reviewId: String
    ): ApiResponse<RatingAndReviewsResponse>

    @POST("v1/rating-review/{reviewId}/product/down-vote")
    suspend fun reviewVoteDown(
        @Header("Authorization") authToken: String,
        @Path("reviewId") reviewId: String
    ): ApiResponse<RatingAndReviewsResponse>

    @DELETE("v1/rating-review/{reviewId}/product/down-vote")
    suspend fun revokeReviewVoteDown(
        @Header("Authorization") authToken: String,
        @Path("reviewId") reviewId: String
    ): ApiResponse<RatingAndReviewsResponse>

    @POST("v1/vouchers/of-consumer/list")
    suspend fun fetchVouchers(
        @Header("Authorization") authKey: String,
        @Body filters: JsonObject
    ): ApiResponse<VoucherResponse>

    @POST("v1/vouchers/collect/{voucher}")
    suspend fun collectVouchers(
        @Header("Authorization") authKey: String,
        @Path("voucher") voucher: String
    ): ApiResponse<Unit>

    @POST("v1/consumers/shipping-details/list")
    suspend fun fetchShippingAddress(
        @Header("Authorization") authKey: String
    ): ApiResponse<ShippingAddressResponse>

    @DELETE("v1/consumers/delete/shipping-detail/{id}")
    suspend fun removeShippingAddress(
        @Header("Authorization") authToken: String,
        @Path("id") id: String
    ): ApiResponse<Unit>

    @POST("v1/orders/info")
    suspend fun fetchOrderInfo(
        @Header("Authorization") authToken: String, @Body orderRequest: OrderRequest
    ): ApiResponse<OrderResponse>

    @POST("v1/orders")
    suspend fun placeOrder(
        @Header("Authorization") authToken: String, @Body orderRequest: OrderRequest
    ): ApiResponse<OrderResponse>

    @POST("v1/orders/for-consumer/list")
    suspend fun fetchOrderListing(
        @Header("Authorization") authToken: String,
        @QueryMap queryMap: Map<String, String>,
        @Body orderListingRequest: JsonObject
    ): ApiResponse<Pagination>

    @POST("v1/orders/for-consumer/{orderId}")
    suspend fun fetchOrder(
        @Header("Authorization") authToken: String,
        @Path("orderId") orderId: String
    ): ApiResponse<OrderResponse>

    @GET("v1/order-settings/consumer/order/consumerReturnReasons")
    suspend fun fetchReturnReasons(): ApiResponse<ReturnReasonsResponse>

    @PATCH("v1/orders/{orderId}/pending_return")
    suspend fun returnOrder(
        @Header("Authorization") authToken: String,
        @Path("orderId") orderId: String,
        @Body req: RequestReturnOrder
    ): ApiResponse<OrderResponse>

    @POST("v1/consumers/save/shipping-detail")
    suspend fun addEditShippingAddress(
        @Header("Authorization") authToken: String,
        @Body shippingAddress: ShippingAddress
    ): ApiResponse<ShippingAddress>

    @GET("v1/consumers/countries/list")
    suspend fun fetchCountries(@Header("Authorization") authToken: String): ApiResponse<CountryResponse>

    @POST("v1/consumers/states-of-country/list")
    suspend fun getStates(
        @Header("Authorization") authToken: String,
        @Body getStatesRequest: GetStatesRequest
    ): ApiResponse<PostStatesResponse>

    @POST("v1/consumers/cities-of-state/list")
    suspend fun getCities(
        @Header("Authorization") authToken: String,
        @Body getCitiesRequest: GetCitiesRequest
    ): ApiResponse<PostCitiesResponse>

    @GET("v1/order-settings/consumer/order/cancellation/reasons")
    suspend fun fetchCancelReasons(): ApiResponse<CancelReasonsResponse>

    @PATCH("v1/orders/{orderId}/vendor/{vendorId}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") authToken: String,
        @Path("orderId") orderId: String,
        @Path("vendorId") vendorId: String,
        @Body reqBody: Map<String, String>
    ): ApiResponse<OrderResponse>

    @GET("v1/consumers/products/search/filters")
    suspend fun fetchSearchFilters(@Query("searchStr") searchStr: String): ApiResponse<SearchFilterResponse>

    @GET("v1/categories/{categoryId}/filters")
    suspend fun fetchCategoryFilters(@Path("categoryId") categoryId: Int): ApiResponse<SearchFilterResponse>

    @GET("v1/consumers/today/deals/filters")
    suspend fun fetchTodayDealsFilters(): ApiResponse<SearchFilterResponse>

    @GET("v1/consumers/under/100/filters")
    suspend fun fetchUnderHundredFilters(): ApiResponse<SearchFilterResponse>

    @GET("v1/consumers/vendor/{vendorId}/filters")
    suspend fun fetchVendorFilters(@Path("vendorId") vendorId: String): ApiResponse<SearchFilterResponse>

    @GET("v1/consumers/me")
    suspend fun fetchProfile(
        @Header("Authorization") authToken: String
    ): ApiResponse<Profile>

    @POST("v1/rating-review/save/{orderId}")
    suspend fun postOrderReview(
        @Header("Authorization") authToken: String,
        @Path("orderId") orderId: String,
        @Body orderReview: OrderReview,
    ): ApiResponse<OrderReview>

    @PUT("v1/consumers")
    suspend fun updateProfile(
        @Header("Authorization") authToken: String,
        @Body reqUpdateUser: RequestUpdateUser
    ): ApiResponse<ResponseUpdateUser>

    @Multipart
    @POST("v1/media/upload/consumer_profile")
    suspend fun uploadImage(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<List<String>>

    @POST("v1/cart/export/pdf")
    suspend fun downloadPdf(
        @Header("Authorization") authToken: String,
        @Body orderRequest: OrderRequest
    ): ResponseBody

    @GET("v1/febys-plus/packages")
    suspend fun fetchFebysPlusPackage(): ApiResponse<FebysPackageResponse>

    @POST("v1/febys-plus/subscribe/{packageId}")
    suspend fun subscribePackage(
        @Header("Authorization") authKey: String,
        @Path("packageId") packageId: String,
        @Body transactionIds: TransactionReq
    ): ApiResponse<Unit>

    @POST("v1/consumers/notifications/list")
    suspend fun fetchNotifications(
        @Header("Authorization") authKey: String,
        @QueryMap queryMap: Map<String, String>,
        @Body request: PagingListRequest
    ): ApiResponse<Pagination>
}