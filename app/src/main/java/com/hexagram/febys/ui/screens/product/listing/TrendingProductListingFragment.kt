package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType

class TrendingProductListingFragment : ProductListingFragment() {
    private val args: TrendingProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.trendingProductsListing {
        setProductItemCount(it.totalRows)
    }

    override fun getFilterType() = FiltersType.SEARCH
}