package com.hexagram.febys.ui.screens.product.listing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListingViewModel @Inject constructor(
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {
    // todo fetch product listing, following is just for ui testing purpose
    private val _observeWishlist = MutableLiveData<DataState<List<Product>>>()
    val observeWishlist: LiveData<DataState<List<Product>>> = _observeWishlist

    fun fetchWishList() = viewModelScope.launch {
        _observeWishlist.postValue(DataState.Loading())
        productRepo.fetchWishList().collect {
            _observeWishlist.postValue(it)
        }
    }
}