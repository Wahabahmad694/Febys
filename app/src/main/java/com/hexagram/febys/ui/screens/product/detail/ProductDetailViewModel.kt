package com.hexagram.febys.ui.screens.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ProductVariant
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {
    var selectedVariant: ProductVariant? = null
    var selectedFirstAttr = ""
    var selectedSecondAttr = ""

    private val _observeProductDetail = MutableLiveData<DataState<Product>>()
    val observeProductDetail: LiveData<DataState<Product>> = _observeProductDetail

    fun fetchProductDetail(productId: Int) = viewModelScope.launch {
        _observeProductDetail.postValue(DataState.Loading())
        productRepo.fetchProductDetail(productId).collect {
            _observeProductDetail.postValue(it)
        }
    }

    fun getFirstAttrList(product: Product): List<String> {
        return product.productVariants
            .asSequence()
            .filter { it.getFirstVariantAttr()?.value != null }
            .map { it.getFirstVariantAttr()!!.value }
            .toSet()
            .toList()
            .sortedBy { it }
    }

    fun getSecondAttrList(selectedFirstAttr: String, product: Product): List<String> {
        return product.productVariants
            .asSequence()
            .filter {
                it.getSecondVariantAttr()?.value != null && it.getFirstVariantAttr()?.value == selectedFirstAttr
            }
            .map { it.getSecondVariantAttr()!!.value }
            .toSet()
            .toList()
            .sortedBy { it }
    }

    fun getVariantByFirstAttr(product: Product): ProductVariant? {
        return product.productVariants.firstOrNull {
            it.getFirstVariantAttr()?.value == selectedFirstAttr
        }
    }

    fun getVariantBySecondAttr(product: Product): ProductVariant? {
        return product.productVariants.firstOrNull {
            it.getFirstVariantAttr()?.value == selectedFirstAttr
                    && it.getSecondVariantAttr()?.value == selectedSecondAttr
        }
    }
}