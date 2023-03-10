package com.hexagram.febys.repos

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.models.api.request.SearchRequest
import com.hexagram.febys.models.api.suggestedSearch.SuggestedProduct
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.SearchService
import com.hexagram.febys.paginations.*
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.utils.update
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductListingRepoImpl @Inject constructor(
    pref: IPrefManger,
    backendService: FebysBackendService,
    val searchService: SearchService
) : ProductRepoImpl(pref, backendService), IProductListingRepo {

    private var totalProduct: MutableLiveData<Long> = MutableLiveData<Long>(-1)

    override fun fetchTodayDealsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            TodayDealsPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun specialProductListing(
        specialFilter: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters, specialFilter)
            SpecialProductListingPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun similarProductListing(
        productId: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            SimilarProductListingPagingSource(
                productId, backendService, req, onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun recommendedProductListing(
        productId: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            RecommendedProductListingPagingSource(
                productId, backendService, req, onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchTrendingProductsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            filters.trendsOnSale = true
            val req = createReq(filters)
            SearchProductPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchEditorsPickListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            filters.editorsPick = true
            val req = createReq(filters)
            SearchProductPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchSameDayDeliveryListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            filters.sameDayDelivery = true
            val req = createReq(filters)
            SearchProductPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchUnder100DollarsItemsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            Under100DollarsItemsPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchStoreYouFollowItemsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authToken = pref.getAccessToken()
            val req = createReq(filters)
            StoreYourFollowPagingSource(authToken, backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun vendorProductListing(
        vendorId: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            VendorProductListingPagingSource(
                vendorId, backendService, req, onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchCategoryProductsListing(
        categoryId: Int,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            CategoryProductsListingPagingSource(
                categoryId, backendService, req, onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun searchProductListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val req = createReq(filters)
            SearchProductPagingSource(backendService, req, onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun getTotalProducts() = totalProduct
    override fun searchProductSuggestionListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        body: SearchRequest
    ): Flow<PagingData<SuggestedProduct>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            SearchProductSuggestionPagingSource(
                searchService,
                body
            ) {
                totalProduct.update(it)
            }
        }.flow
    }

    override fun fetchWishList(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authToken = pref.getAccessToken()
            WishlistPagingSource(
                backendService, authToken, PagingListRequest(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    private fun createReq(
        filters: ProductListingRequest,
        specialFilter: String? = null
    ): PagingListRequest {
        val req = PagingListRequest()
        req.searchStr = filters.searchStr ?: ""
        req.queryStr = filters.searchStr ?: ""
        req.filters = filters.createFilters()
        req.sorter = filters.createSorter()
        if (!specialFilter.isNullOrEmpty()) {
            val specialTypes = JsonArray()
            specialTypes.add(specialFilter)
            val specialTypeFilter = JsonObject()
            specialTypeFilter.add("\$in", specialTypes)
            req.filters!!.add("spacial_types", specialTypeFilter)
        }
        return req
    }
}