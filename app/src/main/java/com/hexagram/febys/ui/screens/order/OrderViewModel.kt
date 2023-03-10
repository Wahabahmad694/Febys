package com.hexagram.febys.ui.screens.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.order.CancelReasons
import com.hexagram.febys.models.api.order.Order
import com.hexagram.febys.models.api.rating.OrderReview
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestReturnOrder
import com.hexagram.febys.repos.IOrderRepo
import com.hexagram.febys.utils.OrderStatus
import com.hexagram.febys.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepo: IOrderRepo
) : BaseViewModel() {
    private val _observerOrder = MutableLiveData<DataState<Order>>()
    val observeOrder: LiveData<DataState<Order>> = _observerOrder

    private val _observerCancelReasons = MutableLiveData<DataState<CancelReasons>>()
    val observeCancelReasons: LiveData<DataState<CancelReasons>> = _observerCancelReasons

    private val _observerCancelOrder = MutableLiveData<DataState<Order>>()
    val observeCancelOrder: LiveData<DataState<Order>> = _observerCancelOrder

    private val _observerOrderReview = MutableLiveData<DataState<OrderReview>>()
    val observeOrderReview: LiveData<DataState<OrderReview>> = _observerOrderReview

    private val _observerReturnReason = MutableLiveData<DataState<List<String>>>()
    val observeReturnReason: LiveData<DataState<List<String>>> = _observerReturnReason

    private val _observerReturnOrder = MutableLiveData<DataState<Order>>()
    val observeReturnOrder: LiveData<DataState<Order>> = _observerReturnOrder

    private val _observerTimer = MutableLiveData<String?>()
    val observeTimer: LiveData<String?> = _observerTimer

    private var orderListOldOrderListing: Flow<PagingData<Order>>? = null
    private var timerJob: Job? = null

    fun fetchOrder(orderId: String) = viewModelScope.launch {
        _observerOrder.postValue(DataState.Loading())
        orderRepo.fetchOrder(orderId).collect {
            _observerOrder.postValue(it)
            if (it is DataState.Data) {
                // showTimer if any item can be cancelled
                val showTimer = it.data.vendorProducts.any { item ->
                    item.reverted == false
                            && item.status in arrayOf(OrderStatus.PENDING, OrderStatus.ACCEPTED)
                }
                val remainingTime =
                    if (showTimer) Utils.DateTime.getRemainingMilliFrom30Min(it.data.createdAt) else -1
                timerJob = startTimer(remainingTime)
            }
        }
    }

    fun fetchCancelReasons() = viewModelScope.launch {
        _observerCancelReasons.postValue(DataState.Loading())
        orderRepo.fetchCancelReasons().collect {
            _observerCancelReasons.postValue(it)
        }
    }

    fun cancelOrder(
        orderId: String, vendorId: String, reason: String, comment: String
    ) = viewModelScope.launch {
        _observerCancelOrder.postValue(DataState.Loading())
        orderRepo.cancelOrder(orderId, vendorId, reason, comment).collect {
            _observerCancelOrder.postValue(it)
        }
    }

    fun fetchOrderList(filters: Array<String>? = null): Flow<PagingData<Order>> {
        if (orderListOldOrderListing == null) {
            orderListOldOrderListing = orderRepo.fetchOrderList(filters, viewModelScope)
        }

        return orderListOldOrderListing!!
    }

    fun postReview(orderId: String, orderReview: OrderReview) = viewModelScope.launch {
        _observerOrderReview.postValue(DataState.Loading())
        orderRepo.postReview(orderId, orderReview).collect {
            _observerOrderReview.postValue(it)
        }
    }

    fun fetchReturnReasons() = viewModelScope.launch {
        _observerReturnReason.postValue(DataState.Loading())
        orderRepo.returnReasons().collect {
            _observerReturnReason.postValue(it)
        }
    }


    fun returnOrder(orderId: String, returnOrderRequest: RequestReturnOrder) =
        viewModelScope.launch {
            _observerReturnOrder.postValue(DataState.Loading())
            orderRepo.returnOrder(orderId, returnOrderRequest).collect {
                _observerReturnOrder.postValue(it)
            }
        }

    fun startTimer(time: Long): Job {
        timerJob?.cancel()
        return viewModelScope.launch {
            if (time <= 0) {
                _observerTimer.postValue(null)
                return@launch
            }

            for (i in time downTo 0 step 1000) {
                val timeInMinAndSec = Utils.DateTime.milliToMin(i)
                _observerTimer.postValue(timeInMinAndSec)
                delay(1000)
            }

            _observerTimer.postValue(null)
        }
    }
}