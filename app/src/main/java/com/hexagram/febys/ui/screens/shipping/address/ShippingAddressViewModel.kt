package com.hexagram.febys.ui.screens.shipping.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.countries.Country
import com.hexagram.febys.models.api.countries.CountryResponse
import com.hexagram.febys.models.api.shippingAddress.PostShippingAddress
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IShippingAddressRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShippingAddressViewModel @Inject constructor(
    private val shippingRepo: IShippingAddressRepo
) : BaseViewModel() {
    private val _shippingAddresses = MutableLiveData<DataState<List<ShippingAddress>>>()
    val shippingAddresses: LiveData<DataState<List<ShippingAddress>>> = _shippingAddresses

    private val _addUpdateShippingAddress = MutableLiveData<DataState<Unit>>()
    val addUpdateShippingAddress: LiveData<DataState<Unit>> = _addUpdateShippingAddress

    private val _countryData = MutableLiveData<DataState<CountryResponse>>()
    val countryResponse: LiveData<DataState<CountryResponse>> = _countryData

    init {
        refreshShippingAddresses()
        getCountries()

    }

    fun refreshShippingAddresses() = viewModelScope.launch {
        _shippingAddresses.postValue(DataState.Loading())
        shippingRepo.fetchShippingAddressRepo().collect {
            _shippingAddresses.postValue(it)
        }
    }

    fun deleteShippingAddresses(shippingAddress: ShippingAddress) = viewModelScope.launch {
        _addUpdateShippingAddress.postValue(DataState.Loading())
        shippingRepo.deleteShippingAddress(shippingAddress).collect {
            _addUpdateShippingAddress.postValue(it)
        }
        refreshShippingAddresses()
    }

    fun setAsDefault(shippingAddress: ShippingAddress) = viewModelScope.launch {
        shippingRepo.setAsDefault(shippingAddress)
    }

    fun updateShippingAddress(shippingAddress: PostShippingAddress) = viewModelScope.launch {
        _addUpdateShippingAddress.postValue(DataState.Loading())
        shippingRepo.updateShippingAddress(shippingAddress).collect {
            _addUpdateShippingAddress.postValue(it)
        }
    }

    fun addShippingAddress(shippingAddress: PostShippingAddress) = viewModelScope.launch {
        _addUpdateShippingAddress.postValue(DataState.Loading())
        shippingRepo.addShippingAddress(shippingAddress).collect {
            _addUpdateShippingAddress.postValue(it)
        }
    }
    fun getCountries() = viewModelScope.launch {
        shippingRepo.fetchCountries(Dispatchers.IO).collect {
            _countryData.postValue(it)
        }
    }

}