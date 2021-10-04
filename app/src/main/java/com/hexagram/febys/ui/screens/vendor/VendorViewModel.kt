package com.hexagram.febys.ui.screens.vendor

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.models.view.VendorListing
import com.hexagram.febys.repos.IVendorRepo
import com.hexagram.febys.repos.ProductListingRepoImpl
import com.hexagram.febys.ui.screens.product.listing.ProductListingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val vendorRepo: IVendorRepo,
    productListingRepo: ProductListingRepoImpl
) : ProductListingViewModel(productListingRepo) {

    private var allCategoryPagingData: Flow<PagingData<VendorListing>>? = null

    fun fetchVendors(isCelebrity: Boolean): Flow<PagingData<VendorListing>> {
        if (allCategoryPagingData == null) {
            allCategoryPagingData = vendorRepo.fetchVendors(isCelebrity, viewModelScope)
        }

        return allCategoryPagingData!!
    }

    fun followVendor(vendorId: Int) =
        viewModelScope.launch { vendorRepo.followVendor(vendorId) }

    fun unFollowVendor(vendorId: Int) =
        viewModelScope.launch { vendorRepo.unFollowVendor(vendorId) }
}