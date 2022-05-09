package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType

class EditorsPickItemsListingFragment : ProductListingFragment() {
    private val args: EditorsPickItemsListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.editorsPickItemListing(refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.EDITORS_PICK
}