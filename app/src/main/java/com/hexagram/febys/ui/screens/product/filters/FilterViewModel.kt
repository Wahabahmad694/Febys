package com.hexagram.febys.ui.screens.product.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.models.api.request.ProductListingRequest
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IFiltersRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val filterRepo: IFiltersRepo
) : BaseViewModel() {
    // set values before calling fetch filters function
    var appliedFilters = ProductListingRequest()
    var filters: Filters? = null
    private val _observeFilters = MutableLiveData<DataState<Filters>>()
    val observeFilters: LiveData<DataState<Filters>> = _observeFilters

    fun fetchFilters(
        filterType: FiltersType, categoryId: Int?, vendorId: String?, searchStr: String? = null
    ) = viewModelScope.launch {
        _observeFilters.postValue(DataState.Loading())
        filterRepo.fetchAllFilters(filterType, categoryId, vendorId, searchStr).collect {
            _observeFilters.postValue(it)
            updateAvailableFilters(it)
        }
    }

    private fun updateAvailableFilters(filters: DataState<Filters>) {
        if (filters is DataState.Data) {
            this.filters = filters.data
        }
    }

    fun notifyFilters() {
        val value = _observeFilters.value ?: return
        _observeFilters.postValue(value)
        updateAvailableFilters(value)
    }
}