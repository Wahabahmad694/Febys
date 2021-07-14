package com.hexagram.febys.ui.screens.product.listing

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.ui.screens.dialog.ErrorDialog
import com.hexagram.febys.utils.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodayDealsListingFragment : ProductListingFragment() {
    private val args: TodayDealsListingFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uiObserver()

        productListingViewModel.fetchToadyDeals()
    }

    private fun uiObserver() {
        productListingViewModel.observeTodayDeals.observe(viewLifecycleOwner) {
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
        val gotoProductListing = TodayDealsListingFragmentDirections.actionToProductDetail(item.id)
        navigateTo(gotoProductListing)
    }

    override fun getListingTitle(): String = args.productListTitle
}