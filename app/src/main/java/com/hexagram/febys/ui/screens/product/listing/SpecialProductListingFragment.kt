package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.ui.screens.menu.MenuFragment
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

@AndroidEntryPoint
class SpecialProductListingFragment : ProductListingFragment() {
    private val args: SpecialProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingData(refresh: Boolean): Flow<PagingData<Product>> {
        return if (args.specialFilter == MenuFragment.SpecialMarketFilters.DISCOUNT_MALL) {
            productListingViewModel.filters.hasPromotion = true
            productListingViewModel.searchProductsListing("", refresh) {
                setProductItemCount(it.totalRows)
            }
        } else {
            productListingViewModel.specialProductListing(refresh, args.specialFilter) {
                setProductItemCount(it.totalRows)
            }
        }
    }

    override fun getFilterType() = FiltersType.SPECIAL_PRODUCT
}