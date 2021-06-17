package com.android.febys.ui.screens.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.febys.base.BaseViewModel
import com.android.febys.dto.ProductDetail
import com.android.febys.network.DataState
import com.android.febys.repos.IProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repo: IProductRepo
) : BaseViewModel() {
    private val _observeProductDetail = MutableLiveData<DataState<ProductDetail>>()
    val observeProductDetail: LiveData<DataState<ProductDetail>> = _observeProductDetail

    fun fetchProductDetail(productId: String) = viewModelScope.launch {
        _observeProductDetail.postValue(DataState.Loading())
        repo.fetchProductDetail(productId).collect {
            _observeProductDetail.postValue(it)
        }
    }
}