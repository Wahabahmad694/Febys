package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.PagingListRequest
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
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            TodayDealsPagingSource(backendService, PagingListRequest(), onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchTrendingProductsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            TrendingProductsPagingSource(
                backendService, PagingListRequest(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchUnder100DollarsItemsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            Under100DollarsItemsPagingSource(
                backendService, PagingListRequest(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchCategoryProductsListing(
        categoryId: Int,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            CategoryProductsListingPagingSource(
                categoryId, backendService, PagingListRequest(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun searchProductListing(
        query: String,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            SearchProductPagingSource(
                backendService, PagingListRequest(searchStr = query), onProductListingResponse
            )
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
}