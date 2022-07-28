package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType

class SearchProductListingFragment : ProductListingFragment() {
    private val args: SearchProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = "Search Results"

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.searchProductsListing(args.query, refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.SEARCH

    override fun getSearchStr(): String = args.query


}