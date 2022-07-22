package com.hexagram.febys.repos

import com.google.gson.JsonObject
import com.hexagram.febys.models.api.banners.Banner
import com.hexagram.febys.models.api.category.UniqueCategory
import com.hexagram.febys.models.api.product.FeaturedCategory
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.product.Trending
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.models.api.vendor.VendorPagingListing
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.FebysWebCustomizationService
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.SeasonalOffer
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(
    private val webCustomizationService: FebysWebCustomizationService,
    private val backendService: FebysBackendService,
    private val pref: IPrefManger,
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
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchTodayDeals(
            pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher): List<FeaturedCategory> {
        val response = backendService.fetchFeaturedCategories()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchFeaturedVendorStores(dispatcher: CoroutineDispatcher): List<Vendor> {
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchVendors(
            pagingListRequest.createQueryMap(),
            JsonObject().apply {
                add("filters",JsonObject().apply { addProperty("featured", true) })
            }

        )
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?.getResponse<VendorPagingListing>()?.vendors ?: emptyList()
    }

    override suspend fun fetchFeaturedCelebrityStores(dispatcher: CoroutineDispatcher): List<Vendor>{
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchCelebrities(
            pagingListRequest.createQueryMap(),
            JsonObject().apply {
                add("filters",JsonObject().apply { addProperty("featured", true) })
            }
        )
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?.getResponse<VendorPagingListing>()?.vendors ?: emptyList()
    }

    override suspend fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher): List<SeasonalOffer> {
        val response = webCustomizationService.fetchAllSeasonalOffers()
        return (response as? ApiResponse.ApiSuccessResponse)?.data ?: emptyList()
    }

    override suspend fun fetchTrendingProductsByUnits(dispatcher: CoroutineDispatcher): List<Product> {
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchTrendingProductsByUnits(
            pagingListRequest.createQueryMap()
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<Trending>()?.getAllProducts() ?: emptyList()
    }

    override suspend fun fetchTrendingProductsBySale(dispatcher: CoroutineDispatcher): List<Product> {
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchTrendingProductsBySale(
            pagingListRequest.createQueryMap()
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<Trending>()?.getAllProducts() ?: emptyList()
    }

    override suspend fun fetchTrendingProducts(dispatcher: CoroutineDispatcher): List<Product> {
        val pagingListRequest = PagingListRequest()
        val productListingRequest = ProductListingRequest()
        productListingRequest.trendsOnSale = true
        pagingListRequest.filters = productListingRequest.createFilters()
        pagingListRequest.sorter = productListingRequest.createSorter()
        val response = backendService.searchProducts(
            pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher): List<Product> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return emptyList()
        val pagingListRequest = PagingListRequest()
        val response =
            backendService.fetchStoreYouFollow(authToken, pagingListRequest.createQueryMap())
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher): List<Product> {
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchUnder100DollarsItems(
            pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchEditorsPickItems(dispatcher: CoroutineDispatcher): List<Product> {
        val pagingListRequest = PagingListRequest()
        val productListingRequest = ProductListingRequest()
        productListingRequest.editorsPick = true
        pagingListRequest.filters = productListingRequest.createFilters()
        pagingListRequest.sorter = productListingRequest.createSorter()
        val response = backendService.searchProducts(
            pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchSameDayDeliveryItems(dispatcher: CoroutineDispatcher): List<Product> {
        val pagingListRequest = PagingListRequest()
        val productListingRequest = ProductListingRequest()
        productListingRequest.sameDayDelivery = true
        pagingListRequest.filters = productListingRequest.createFilters()
        pagingListRequest.sorter = productListingRequest.createSorter()
        val response = backendService.searchProducts(
            pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

}