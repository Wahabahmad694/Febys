package com.hexagram.febys.ui.screens.product

import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.repos.IProductRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class ProductViewModel @Inject constructor(
    val productRepo: IProductRepo
) : BaseViewModel() {
}