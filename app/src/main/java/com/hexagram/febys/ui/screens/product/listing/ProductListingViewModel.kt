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
    private val _observeWishlist = MutableLiveData<DataState<List<Product>>>()
    val observeWishlist: LiveData<DataState<List<Product>>> = _observeWishlist

    private val _observeTodayDeals = MutableLiveData<DataState<List<Product>>>()
    val observeTodayDeals: LiveData<DataState<List<Product>>> = _observeTodayDeals

    private val _observeTrendingProducts = MutableLiveData<DataState<List<Product>>>()
    val observeTrendingProducts: LiveData<DataState<List<Product>>> = _observeTrendingProducts

    private val _observeUnder100DollarsItems = MutableLiveData<DataState<List<Product>>>()
    val observeUnder100DollarsItems: LiveData<DataState<List<Product>>> =
        _observeUnder100DollarsItems

    fun fetchWishList() = viewModelScope.launch {
        _observeWishlist.postValue(DataState.Loading())
        productRepo.fetchWishList().collect {
            _observeWishlist.postValue(it)
        }
    }

    fun fetchToadyDeals() {
        viewModelScope.launch {
            _observeTodayDeals.postValue(DataState.Loading())
            productRepo.fetchTodayDeals().collect {
                _observeTodayDeals.postValue(it)
            }
        }
    }

    fun fetchTrendingProducts() {
        viewModelScope.launch {
            _observeTrendingProducts.postValue(DataState.Loading())
            productRepo.fetchTrendingProducts().collect {
                _observeTrendingProducts.postValue(it)
            }
        }
    }

    fun fetchUnder100DollarsItems() {
        viewModelScope.launch {
            _observeUnder100DollarsItems.postValue(DataState.Loading())
            productRepo.fetchUnder100DollarsItems().collect {
                _observeUnder100DollarsItems.postValue(it)
            }
        }
    }
}