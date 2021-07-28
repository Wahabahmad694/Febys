package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
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
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            TodayDealsPagingSource(backendService, RequestOfPagination(), onProductListingResponse)
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchTrendingProductsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            TrendingProductsPagingSource(
                backendService, RequestOfPagination(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchUnder100DollarsItemsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            Under100DollarsItemsPagingSource(
                backendService, RequestOfPagination(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchCategoryProductsListing(
        categoryId: Int,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            CategoryProductsListingPagingSource(
                backendService, categoryId, RequestOfPagination(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchWishList(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        onProductListingResponse: ((ResponseProductListing) -> Unit)?
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authToken = pref.getAccessToken()
            WishlistPagingSource(
                backendService, authToken, RequestOfPagination(), onProductListingResponse
            )
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }
}