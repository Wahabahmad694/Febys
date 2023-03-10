package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType

class CategoryProductListingFragment : ProductListingFragment() {
    private val args: CategoryProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.categoryProductsListing(args.categoryId, refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.CATEGORY

    override fun getCategoryId() = args.categoryId
}