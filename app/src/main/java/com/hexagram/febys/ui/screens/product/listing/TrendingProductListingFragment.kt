package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs

class TrendingProductListingFragment : ProductListingFragment() {
    private val args: TrendingProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.trendingProductsListing {
        setProductItemCount(it.totalRows)
    }
}