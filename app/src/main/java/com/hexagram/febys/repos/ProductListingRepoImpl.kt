package com.hexagram.febys.repos

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
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.paginations.*
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductListingRepoImpl @Inject constructor(
    pref: IPrefManger,
    backendService: FebysBackendService
) : ProductRepoImpl(pref, backendService), IProductListingRepo {
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
            TodayDealsPagingSource(backendService, req, onProductListingResponse)
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
            val req = createReq(filters)
            TrendingProductsPagingSource(backendService, req, onProductListingResponse)
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