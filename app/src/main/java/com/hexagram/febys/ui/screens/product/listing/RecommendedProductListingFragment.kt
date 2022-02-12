package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendedProductListingFragment : ProductListingFragment() {
    private val args: RecommendedProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.recommendedProductListing(args.productId, refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.RECOMMENDED_PRODUCT
}