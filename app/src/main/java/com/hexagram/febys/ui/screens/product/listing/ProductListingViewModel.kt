package com.hexagram.febys.ui.screens.product.listing

import androidx.lifecycle.viewModelScope
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductListingViewModel @Inject constructor(
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {
    val todayDealsListing = productRepo.fetchTodayDealsListing(viewModelScope)
    val trendingProductsListing = productRepo.fetchTrendingProductsListing(viewModelScope)
    val under100DollarsItemsListing = productRepo.fetchUnder100DollarsItemsListing(viewModelScope)

    fun categoryProductsListing(categoryId: Int) =
        productRepo.fetchCategoryProductsListing(categoryId, viewModelScope)
}