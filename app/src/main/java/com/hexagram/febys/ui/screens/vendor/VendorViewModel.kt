package com.hexagram.febys.ui.screens.vendor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.models.view.VendorDetail
import com.hexagram.febys.models.view.VendorListing
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IVendorRepo
import com.hexagram.febys.repos.ProductListingRepoImpl
import com.hexagram.febys.ui.screens.product.listing.ProductListingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val vendorRepo: IVendorRepo,
    productListingRepo: ProductListingRepoImpl
) : ProductListingViewModel(productListingRepo) {

    private var allCategoryPagingData: Flow<PagingData<VendorListing>>? = null
    private val _observerVendorDetail = MutableLiveData<DataState<VendorDetail>>()
    val observerVendorDetail: LiveData<DataState<VendorDetail>> = _observerVendorDetail

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

    fun fetchVendorDetail(vendorId: Int) = viewModelScope.launch {
        _observerVendorDetail.postValue(DataState.Loading())
        vendorRepo.fetchVendorDetail(vendorId).collect {
            _observerVendorDetail.postValue(it)
        }
    }
}