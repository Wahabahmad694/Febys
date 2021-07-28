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


    fun toggleFav(variantId: Int) = viewModelScope.launch {
        productRepo.toggleFav(variantId)
    }

    fun getFav(): MutableSet<Int> = productRepo.getFav()
}