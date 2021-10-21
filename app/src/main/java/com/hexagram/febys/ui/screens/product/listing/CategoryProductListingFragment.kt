package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs

class CategoryProductListingFragment : ProductListingFragment() {
    private val args: CategoryProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() =
        productListingViewModel.categoryProductsListing(args.categoryId) {
            setProductItemCount(it.totalRows)
        }
}