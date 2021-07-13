package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.navigateTo

class Under100DollarsItemListingFragment : ProductListingFragment() {
    private val args: Under100DollarsItemListingFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productListingViewModel.fetchUnder100DollarsItems()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiObserver()
    }

    private fun uiObserver() {
        productListingViewModel.observeUnder100DollarsItems.observe(viewLifecycleOwner) {
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
            Under100DollarsItemListingFragmentDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }

    override fun getListingTitle(): String = args.productListTitle
}