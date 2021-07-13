package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.dialog.ErrorDialog

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

    override fun getListingTitle(): String = args.productListTitle
}