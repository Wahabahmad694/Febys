package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType

class Under100DollarsItemListingFragment : ProductListingFragment() {
    private val args: Under100DollarsItemListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.under100DollarsItemsListing(refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.SEARCH
}