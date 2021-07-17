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
    val todayDealsListing = productRepo.fetchTodayDealsListing(viewModelScope)
    val trendingProductsListing = productRepo.fetchTrendingProductsListing(viewModelScope)
    val under100DollarsItemsListing = productRepo.fetchUnder100DollarsItemsListing(viewModelScope)
    private var categoryProductsListing: Flow<PagingData<Product>>? = null

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