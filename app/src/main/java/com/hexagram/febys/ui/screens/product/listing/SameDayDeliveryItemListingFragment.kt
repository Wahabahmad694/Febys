package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType

class SameDayDeliveryItemListingFragment: ProductListingFragment() {
    private val args: SameDayDeliveryItemListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean) =
        productListingViewModel.sameDayDeliveryItemListing(refresh) {
            setProductItemCount(it.totalRows)
        }

    override fun getFilterType() = FiltersType.SAME_DAY_DELIVERY
}