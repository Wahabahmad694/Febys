package com.hexagram.febys.ui.screens.product.listing

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProductListingViewModel @Inject constructor(
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {
    private var todayDealsListing: Flow<PagingData<Product>>? = null
    private var trendingProductsListing: Flow<PagingData<Product>>? = null
    private var under100DollarsItemsListing: Flow<PagingData<Product>>? = null
    private var categoryProductsListing: Flow<PagingData<Product>>? = null

    fun todayDealsListing(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (todayDealsListing == null) {
            todayDealsListing =
                productRepo.fetchTodayDealsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return todayDealsListing!!
    }

    fun trendingProductsListing(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (trendingProductsListing == null) {
            trendingProductsListing =
                productRepo.fetchTrendingProductsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return trendingProductsListing!!
    }

    fun under100DollarsItemsListing(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (under100DollarsItemsListing == null) {
            under100DollarsItemsListing =
                productRepo.fetchUnder100DollarsItemsListing(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return under100DollarsItemsListing!!
    }

    fun categoryProductsListing(
        categoryId: Int, onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (categoryProductsListing == null) {
            categoryProductsListing =
                productRepo.fetchCategoryProductsListing(
                    categoryId, viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return categoryProductsListing!!
    }
}