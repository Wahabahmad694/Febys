package com.hexagram.febys.ui.screens.vendor

import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.repos.IVendorRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val vendorRepo: IVendorRepo
) : BaseViewModel() {

    val allCategoryPagingData = vendorRepo.fetchVendors(viewModelScope)
}