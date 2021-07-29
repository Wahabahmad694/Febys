package com.hexagram.febys.ui.screens.product.listing

import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.utils.navigateTo

class CategoryProductListingFragment : ProductListingFragment() {
    private val args: CategoryProductListingFragmentArgs by navArgs()

    override fun getListingTitle(): String = args.productListTitle

    override fun getProductPagingDate() =
        productListingViewModel.categoryProductsListing(args.categoryId) {
            setProductItemCount(it.totalRows)
        }
}