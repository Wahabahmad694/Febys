package com.android.febys.ui.screens.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.febys.network.DataState
import com.android.febys.network.domain.models.Product
import com.android.febys.repos.IProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val repo: IProductRepo
) : ViewModel() {
    private val _observeWishlist = MutableLiveData<DataState<List<Product>>>()
    val observeWishlist: LiveData<DataState<List<Product>>> = _observeWishlist

    fun fetchWishList() = viewModelScope.launch {
        _observeWishlist.postValue(DataState.loading())
        repo.fetchWishList().collect {
            _observeWishlist.postValue(it)
        }
    }
}