package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.navigateTo

class TrendingProductListingFragment : ProductListingFragment() {
    private val args: TrendingProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.trendingProductsListing {
        binding.productListingCount = it.totalRows
    }

    override fun onProductClick(position: Int, item: Product) {
        val gotoProductListing =
            TrendingProductListingFragmentDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }
}