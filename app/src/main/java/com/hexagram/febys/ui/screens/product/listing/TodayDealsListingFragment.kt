package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodayDealsListingFragment : ProductListingFragment() {
    private val args: TodayDealsListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() = productListingViewModel.todayDealsListing {
        setProductItemCount(it.totalRows)
    }

    override fun getFilterType() = FiltersType.TODAY_DEALS
}