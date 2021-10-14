package com.hexagram.febys.ui.screens.payment.method

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IPaymentMethodRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val paymentMethodRepo: IPaymentMethodRepo
) : BaseViewModel() {
    private val _observePaymentMethods = MutableLiveData<DataState<List<PaymentMethod>>>()
    val observePaymentMethods: LiveData<DataState<List<PaymentMethod>>> = _observePaymentMethods

    init {
        fetchPaymentMethods()
    }

    fun fetchPaymentMethods() = viewModelScope.launch {
        _observePaymentMethods.postValue(DataState.Loading())
        paymentMethodRepo.fetchPaymentMethods().collect {
            _observePaymentMethods.postValue(it)
        }
    }

    fun setAsDefault(id: Int) {
        viewModelScope.launch {
            paymentMethodRepo.setAsDefault(id)
        }
    }
}