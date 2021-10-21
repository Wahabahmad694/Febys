package com.hexagram.febys.ui.screens.product

import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.repos.IProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class ProductViewModel @Inject constructor(
    val productRepo: IProductRepo
) : BaseViewModel() {

    fun toggleFav(skuId: String) = viewModelScope.launch {
        productRepo.toggleFav(skuId)
    }

    fun addToFav(skuId: String) = viewModelScope.launch {
        productRepo.addToFav(skuId)
    }

    fun removeFromFav(skuId: String) = viewModelScope.launch {
        productRepo.removeFromFav(skuId)
    }

    fun getFav(): MutableSet<String> = productRepo.getFav()

    fun isFavProduct(skuId: String) = skuId in getFav()
}