package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.NavGraphDirections
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.navigateTo

class SearchProductListingFragment : ProductListingFragment() {
    private val args: SearchProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.query

    override fun getProductPagingDate() =
        productListingViewModel.searchProductsListing(args.query) {
            setProductItemCount(it.totalRows)
        }

    override fun onProductClick(position: Int, item: Product) {
        val gotoProductListing =
            NavGraphDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }
}