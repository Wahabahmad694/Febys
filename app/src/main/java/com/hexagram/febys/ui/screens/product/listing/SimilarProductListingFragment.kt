package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimilarProductListingFragment : ProductListingFragment() {
    private val args: SimilarProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.similarProductListing(args.productId, refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.SIMILAR_PRODUCT
}