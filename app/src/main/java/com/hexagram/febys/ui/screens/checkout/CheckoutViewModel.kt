package com.hexagram.febys.ui.screens.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.view.CheckoutModel
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.ICartRepo
import com.hexagram.febys.repos.ICheckoutRepo
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.cart.CartViewModel
import com.hexagram.febys.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val checkoutRepo: ICheckoutRepo, cartRepo: ICartRepo, productRepo: IProductRepo
) : CartViewModel(cartRepo, productRepo) {

    private val _observeCheckoutModel = MutableLiveData<Event<DataState<CheckoutModel>>>()
    val observeCheckoutModel: LiveData<Event<DataState<CheckoutModel>>> = _observeCheckoutModel

    init {
        fetchCheckoutModel()
    }

    private fun fetchCheckoutModel() = viewModelScope.launch {
        _observeCheckoutModel.postValue(Event(DataState.Loading()))
        checkoutRepo.fetchCheckoutModel().collect {
            _observeCheckoutModel.postValue(Event(it))
        }
    }
}