package com.hexagram.febys.ui.screens.product.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.repos.IProductListingRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
open class ProductListingViewModel @Inject constructor(
    private val productListingRepo: IProductListingRepo
) : ProductViewModel(productListingRepo) {
    private var todayDealsListing: Flow<PagingData<Product>>? = null
    private var specialProductListing: Flow<PagingData<Product>>? = null
    private var similarProductListing: Flow<PagingData<Product>>? = null
    private var recommendedProductListing: Flow<PagingData<Product>>? = null
    private var trendingProductsListing: Flow<PagingData<Product>>? = null
    private var under100DollarsItemsListing: Flow<PagingData<Product>>? = null
    private var storeYouFollowItemListingItemsListing: Flow<PagingData<Product>>? = null
    private var categoryProductsListing: Flow<PagingData<Product>>? = null
    private var searchProductsListing: Flow<PagingData<Product>>? = null
    private var vendorProductsListing: Flow<PagingData<Product>>? = null

    private val _observeItemCount = MutableLiveData<Int>()
    val observeItemCount: LiveData<Int> = _observeItemCount

    var filters = ProductListingRequest()

    fun todayDealsListing(
        refresh: Boolean, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (todayDealsListing == null || refresh) {
            todayDealsListing =
                productListingRepo.fetchTodayDealsListing(
                    filters, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return todayDealsListing!!
    }

    fun specialProductListing(
        refresh: Boolean, specialFilter: String,
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (specialProductListing == null || refresh) {
            specialProductListing =
                productListingRepo.specialProductListing(
                    specialFilter,
                    filters,
                    viewModelScope,
                    onProductListingResponse = onProductListingResponse
                )
        }

        return specialProductListing!!
    }

    fun similarProductListing(
        productId: String,
        refresh: Boolean,
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (similarProductListing == null || refresh) {
            similarProductListing =
                productListingRepo.similarProductListing(
                    productId,
                    filters,
                    viewModelScope,
                    onProductListingResponse = onProductListingResponse
                )
        }

        return similarProductListing!!
    }

    fun recommendedProductListing(
        productId: String,
        refresh: Boolean,
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (recommendedProductListing == null || refresh) {
            recommendedProductListing =
                productListingRepo.recommendedProductListing(
                    productId,
                    filters,
                    viewModelScope,
                    onProductListingResponse = onProductListingResponse
                )
        }

        return recommendedProductListing!!
    }

    fun trendingProductsListing(
        refresh: Boolean, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (trendingProductsListing == null || refresh) {
            trendingProductsListing =
                productListingRepo.fetchTrendingProductsListing(
                    filters, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return trendingProductsListing!!
    }

    fun storeYouFollowListing(
        refresh: Boolean, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (trendingProductsListing == null || refresh) {
            trendingProductsListing =
                productListingRepo.fetchTrendingProductsListing(
                    filters, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return trendingProductsListing!!
    }

    fun under100DollarsItemsListing(
        refresh: Boolean, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (under100DollarsItemsListing == null || refresh) {
            under100DollarsItemsListing =
                productListingRepo.fetchUnder100DollarsItemsListing(
                    filters, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return under100DollarsItemsListing!!
    }

    fun categoryProductsListing(
        categoryId: Int,
        refresh: Boolean,
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (categoryProductsListing == null || refresh) {
            categoryProductsListing =
                productListingRepo.fetchCategoryProductsListing(
                    categoryId,
                    filters,
                    viewModelScope,
                    onProductListingResponse = onProductListingResponse
                )
        }

        return categoryProductsListing!!
    }

    fun searchProductsListing(
        query: String,
        refresh: Boolean,
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (searchProductsListing == null || refresh) {
            filters.searchStr = query
            searchProductsListing =
                productListingRepo.searchProductListing(
                    filters, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return searchProductsListing!!
    }

    fun vendorProductListing(
        vendorId: String,
        refresh: Boolean,
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (vendorProductsListing == null || refresh) {
            vendorProductsListing =
                productListingRepo.vendorProductListing(
                    vendorId,
                    filters,
                    viewModelScope,
                    onProductListingResponse = onProductListingResponse
                )
        }

        return vendorProductsListing!!
    }

    fun updateItemCount(count: Int) {
        _observeItemCount.postValue(count)
    }
}