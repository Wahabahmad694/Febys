package com.hexagram.febys.ui.screens.product.listing

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.network.response.OldProduct
import com.hexagram.febys.network.response.ResponseProductListing
import com.hexagram.febys.repos.IProductListingRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
open class ProductListingViewModel @Inject constructor(
    private val productListingRepo: IProductListingRepo
) : ProductViewModel(productListingRepo) {
    private var todayDealsListing: Flow<PagingData<OldProduct>>? = null
    private var trendingProductsListing: Flow<PagingData<OldProduct>>? = null
    private var under100DollarsItemsListing: Flow<PagingData<OldProduct>>? = null
    private var categoryProductsListing: Flow<PagingData<OldProduct>>? = null
    private var searchProductsListing: Flow<PagingData<OldProduct>>? = null
    private var vendorProductsListing: Flow<PagingData<OldProduct>>? = null

    fun todayDealsListing(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<OldProduct>> {
        if (todayDealsListing == null) {
            todayDealsListing =
                productListingRepo.fetchTodayDealsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return todayDealsListing!!
    }

    fun trendingProductsListing(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<OldProduct>> {
        if (trendingProductsListing == null) {
            trendingProductsListing =
                productListingRepo.fetchTrendingProductsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return trendingProductsListing!!
    }

    fun under100DollarsItemsListing(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<OldProduct>> {
        if (under100DollarsItemsListing == null) {
            under100DollarsItemsListing =
                productListingRepo.fetchUnder100DollarsItemsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return under100DollarsItemsListing!!
    }

    fun categoryProductsListing(
        categoryId: Int, onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<OldProduct>> {
        if (categoryProductsListing == null) {
            categoryProductsListing =
                productListingRepo.fetchCategoryProductsListing(
                    categoryId, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return categoryProductsListing!!
    }

    fun searchProductsListing(
        query: String, onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<OldProduct>> {
        if (searchProductsListing == null) {
            searchProductsListing =
                productListingRepo.searchProductListing(
                    query, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return searchProductsListing!!
    }

    fun vendorProductListing(
        vendorId: Int, onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<OldProduct>> {
        if (vendorProductsListing == null) {
            vendorProductsListing =
                productListingRepo.fetchUnder100DollarsItemsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return vendorProductsListing!!
    }
}