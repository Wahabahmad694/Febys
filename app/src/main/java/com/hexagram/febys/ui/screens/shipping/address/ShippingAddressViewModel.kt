package com.hexagram.febys.ui.screens.shipping.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IShippingAddressRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val shippingRepo: IShippingAddressRepo
) : BaseViewModel() {
    private val _shippingAddresses = MutableLiveData<DataState<List<ShippingAddress>>>()
    val shippingAddresses: LiveData<DataState<List<ShippingAddress>>> = _shippingAddresses

    init {
        refreshShippingAddresses()
    }

    private fun refreshShippingAddresses() = viewModelScope.launch {
        _shippingAddresses.postValue(DataState.Loading())
        shippingRepo.fetchShippingAddressRepo().collect {
            _shippingAddresses.postValue(it)
        }
    }

    fun setAsDefault(id: Int) = viewModelScope.launch {
        shippingRepo.setAsDefault(id)
    }
}