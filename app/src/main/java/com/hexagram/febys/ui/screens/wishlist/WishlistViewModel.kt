package com.hexagram.febys.ui.screens.wishlist

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
class WishlistViewModel @Inject constructor(
    private val repo: IProductRepo
) : com.hexagram.febys.base.BaseViewModel() {
    private val _observeWishlist = MutableLiveData<DataState<List<Product>>>()
    val observeWishlist: LiveData<DataState<List<Product>>> = _observeWishlist

    fun fetchWishList() = viewModelScope.launch {
        _observeWishlist.postValue(DataState.Loading())
        repo.fetchWishList().collect {
            _observeWishlist.postValue(it)
        }
    }
}