package com.hexagram.febys.ui.screens.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IOrderRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepo: IOrderRepo
) : BaseViewModel() {
    private val _observerOrders = MutableLiveData<DataState<List<Order>>>()
    val observeOrders: LiveData<DataState<List<Order>>> = _observerOrders

    private val _observerOrder = MutableLiveData<DataState<Order>>()
    val observeOrder: LiveData<DataState<Order>> = _observerOrder

    fun fetchOrders(filters: Array<String>? = null) = viewModelScope.launch {
        _observerOrders.postValue(DataState.Loading())
        orderRepo.fetchOrders(filters).collect {
            _observerOrders.postValue(it)
        }
    }

    fun fetchOrder(orderId: String) = viewModelScope.launch {
        _observerOrder.postValue(DataState.Loading())
        orderRepo.fetchOrder(orderId).collect {
            _observerOrder.postValue(it)
        }
    }
}