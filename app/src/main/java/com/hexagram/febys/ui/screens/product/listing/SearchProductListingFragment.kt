package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs

class SearchProductListingFragment : ProductListingFragment() {
    private val args: SearchProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.query

    override fun getProductPagingDate() =
        productListingViewModel.searchProductsListing(args.query) {
            setProductItemCount(it.totalRows)
        }
}