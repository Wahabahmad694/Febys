package com.hexagram.febys.ui.screens.vendor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.models.api.vendor.Endorsement
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IProductListingRepo
import com.hexagram.febys.repos.IVendorRepo
import com.hexagram.febys.ui.screens.product.listing.ProductListingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val vendorRepo: IVendorRepo,
    productListingRepo: IProductListingRepo
) : ProductListingViewModel(productListingRepo) {

    private var allVendorsPagingData: Flow<PagingData<Any>>? = null
    private val _observerVendorDetail = MutableLiveData<DataState<Vendor>>()
    val observerVendorDetail: LiveData<DataState<Vendor>> = _observerVendorDetail

    private val _observerVendorEndorsement = MutableLiveData<DataState<List<Endorsement>>>()
    val observerVendorEndorsement: LiveData<DataState<List<Endorsement>>> =
        _observerVendorEndorsement

    fun fetchVendors(isCelebrity: Boolean): Flow<PagingData<Any>> {
        if (allVendorsPagingData == null) {
            allVendorsPagingData = vendorRepo.fetchVendors(isCelebrity, viewModelScope)
        }

        return allVendorsPagingData!!
    }

    fun followVendor(vendorId: String) =
        viewModelScope.launch { vendorRepo.followVendor(vendorId) }

    fun unFollowVendor(vendorId: String) =
        viewModelScope.launch { vendorRepo.unFollowVendor(vendorId) }

    fun fetchVendorDetail(vendorId: String) = viewModelScope.launch {
        _observerVendorDetail.postValue(DataState.Loading())
        vendorRepo.fetchVendorDetail(vendorId).collect {
            _observerVendorDetail.postValue(it)
        }
    }

    fun fetchVendorEndorsement(vendorId: String) = viewModelScope.launch {
        vendorRepo.fetchVendorEndorsement(vendorId).collect {
            _observerVendorEndorsement.postValue(it)
        }
    }
}