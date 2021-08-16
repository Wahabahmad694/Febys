package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs

class Under100DollarsItemListingFragment : ProductListingFragment() {
    private val args: Under100DollarsItemListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.under100DollarsItemsListing {
        setProductItemCount(it.totalRows)
    }
}