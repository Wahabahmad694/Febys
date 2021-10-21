package com.hexagram.febys.ui.screens.product.listing

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
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
    private var trendingProductsListing: Flow<PagingData<Product>>? = null
    private var under100DollarsItemsListing: Flow<PagingData<Product>>? = null
    private var categoryProductsListing: Flow<PagingData<Product>>? = null
    private var searchProductsListing: Flow<PagingData<Product>>? = null
    private var vendorProductsListing: Flow<PagingData<Product>>? = null

    fun todayDealsListing(
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (todayDealsListing == null) {
            todayDealsListing =
                productListingRepo.fetchTodayDealsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return todayDealsListing!!
    }

    fun trendingProductsListing(
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (trendingProductsListing == null) {
            trendingProductsListing =
                productListingRepo.fetchTrendingProductsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return trendingProductsListing!!
    }

    fun under100DollarsItemsListing(
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (under100DollarsItemsListing == null) {
            under100DollarsItemsListing =
                productListingRepo.fetchUnder100DollarsItemsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return under100DollarsItemsListing!!
    }

    fun categoryProductsListing(
        categoryId: Int, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (categoryProductsListing == null) {
            categoryProductsListing =
                productListingRepo.fetchCategoryProductsListing(
                    categoryId, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return categoryProductsListing!!
    }

    fun searchProductsListing(
        query: String, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (searchProductsListing == null) {
            searchProductsListing =
                productListingRepo.searchProductListing(
                    query, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return searchProductsListing!!
    }

    fun vendorProductListing(
        vendorId: Int, onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (vendorProductsListing == null) {
            vendorProductsListing =
                productListingRepo.fetchUnder100DollarsItemsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return vendorProductsListing!!
    }
}