package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.navigateTo

class Under100DollarsItemListingFragment : ProductListingFragment() {
    private val args: Under100DollarsItemListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.under100DollarsItemsListing

    override fun onProductClick(position: Int, item: Product) {
        val gotoProductListing =
            Under100DollarsItemListingFragmentDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }
}