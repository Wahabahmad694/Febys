package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.navigateTo

class CategoryProductListingFragment : ProductListingFragment() {
    private val args: CategoryProductListingFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo change to category product listing
        productListingViewModel.fetchWishList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiObserver()
    }

    private fun uiObserver() {
        // todo change observer to category product listing
        productListingViewModel.observeWishlist.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Loading -> {

                }
                is DataState.Error -> {
                    ErrorDialog(it).show(childFragmentManager, ErrorDialog.TAG)
                }
                is DataState.Data -> {
                    val productList = it.data
                    binding.productListingCount = productList.size
                    productListingAdapter.submitList(productList)
                }
            }
        }
    }

    override fun onProductClick(position: Int, item: Product) {
        val gotoProductListing =
            CategoryProductListingFragmentDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }

    override fun getListingTitle(): String = args.productListTitle
}