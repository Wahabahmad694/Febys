package com.hexagram.febys.repos

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.models.api.request.SearchRequest
import com.hexagram.febys.models.api.suggestedSearch.SuggestedProduct
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductListingRepo : IProductRepo {
    fun fetchTodayDealsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun specialProductListing(
        specialFilter: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun similarProductListing(
        productId: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun recommendedProductListing(
        productId: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchTrendingProductsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchEditorsPickListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchSameDayDeliveryListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchUnder100DollarsItemsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchStoreYouFollowItemsListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun vendorProductListing(
        vendorId: String,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>?

    fun fetchCategoryProductsListing(
        categoryId: Int,
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun searchProductListing(
        filters: ProductListingRequest,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

    fun fetchWishList(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        onProductListingResponse: ((ProductPagingListing) -> Unit)?
    ): Flow<PagingData<Product>>

     fun searchProductSuggestionListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher,
        body: SearchRequest
    ): Flow<PagingData<SuggestedProduct>>

    fun getTotalProducts(): MutableLiveData<Long>
}