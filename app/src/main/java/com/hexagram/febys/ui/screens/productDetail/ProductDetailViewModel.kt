package com.hexagram.febys.ui.screens.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.repos.IProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepo: IProductRepo
) : com.hexagram.febys.base.BaseViewModel() {
    private val _observeProductDetail = MutableLiveData<DataState<Product>>()
    val observeProductDetail: LiveData<DataState<Product>> = _observeProductDetail

    fun fetchProductDetail(productId: Int) = viewModelScope.launch {
        _observeProductDetail.postValue(DataState.Loading())
        productRepo.fetchProductDetail(productId).collect {
            _observeProductDetail.postValue(it)
        }
    }
}