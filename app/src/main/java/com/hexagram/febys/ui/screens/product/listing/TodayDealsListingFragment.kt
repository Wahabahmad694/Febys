package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodayDealsListingFragment : ProductListingFragment() {
    private val args: TodayDealsListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.todayDealsListing

    override fun onProductClick(position: Int, item: Product) {
        val gotoProductListing = TodayDealsListingFragmentDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }
}