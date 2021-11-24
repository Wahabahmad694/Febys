package com.hexagram.febys.ui.screens.shipping.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.cities.PostCitiesResponse
import com.hexagram.febys.models.api.countries.CountryResponse
import com.hexagram.febys.models.api.request.GetCitiesRequest
import com.hexagram.febys.models.api.request.GetStatesRequest
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.states.PostStatesResponse
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

    private val _statesData = MutableLiveData<DataState<PostStatesResponse>>()
    val statesResponse: LiveData<DataState<PostStatesResponse>> = _statesData

    private val _citiesData = MutableLiveData<DataState<PostCitiesResponse>>()
    val citiesResponse: LiveData<DataState<PostCitiesResponse>> = _citiesData

    init {
        refreshShippingAddresses()
        fetchCountries()
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
        _addUpdateShippingAddress.postValue(DataState.Loading())
        shippingRepo.addEditShippingAddress(shippingAddress).collect {
            _addUpdateShippingAddress.postValue(it)
        }
        refreshShippingAddresses()
    }

    fun addEditShippingAddress(shippingAddress: ShippingAddress) = viewModelScope.launch {
        _addUpdateShippingAddress.postValue(DataState.Loading())
        shippingRepo.addEditShippingAddress(shippingAddress).collect {
            _addUpdateShippingAddress.postValue(it)
        }
    }

    private fun fetchCountries() = viewModelScope.launch {
        _countryData.postValue(DataState.Loading())
        shippingRepo.fetchCountries(Dispatchers.IO).collect {
            _countryData.postValue(it)
        }
    }

    fun fetchStates(getStatesRequest: GetStatesRequest) = viewModelScope.launch {
        _statesData.postValue(DataState.Loading())
        shippingRepo.getStates(getStatesRequest).collect {
            _statesData.postValue(it)
        }
    }

    fun fetchCities(getCitiesRequest: GetCitiesRequest) = viewModelScope.launch {
        _citiesData.postValue(DataState.Loading())
        shippingRepo.getCities(getCitiesRequest).collect {
            _citiesData.postValue(it)
        }
    }
}