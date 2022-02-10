package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpecialProductListingFragment : ProductListingFragment() {
    private val args: SpecialProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.specialProductListing(refresh, args.specialFilter) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.SPECIAL_PRODUCT
}