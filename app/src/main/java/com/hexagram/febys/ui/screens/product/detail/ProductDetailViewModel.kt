package com.hexagram.febys.ui.screens.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.QuestionAnswers
import com.hexagram.febys.models.api.product.Variant
import com.hexagram.febys.network.DataState
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
    var selectedVariant: Variant? = null
    var selectedFirstAttr = ""
    var selectedSecondAttr = ""

    private val _observeProductDetail = MutableLiveData<DataState<Product>>()
    val observeOldProductDetail: LiveData<DataState<Product>> = _observeProductDetail

    private val _observeAskQuestion = MutableLiveData<DataState<QuestionAnswers>>()
    val observeAskQuestion: LiveData<DataState<QuestionAnswers>> = _observeAskQuestion

    fun fetchProductDetail(productId: String) = viewModelScope.launch {
        _observeProductDetail.postValue(DataState.Loading())
        productRepo.fetchProductDetail(productId).collect {
            _observeProductDetail.postValue(it)
        }
    }

    fun getFirstAttrList(product: Product): List<String> {
        return product.variants
            .filter { it.getFirstVariantAttr()?.value != null }
            .map { it.getFirstVariantAttr()!!.value }
            .toSet()
            .toList()
    }

    fun getSecondAttrList(selectedFirstAttr: String, product: Product): List<String> {
        return product.variants
            .filter {
                it.getSecondVariantAttr()?.value != null && it.getFirstVariantAttr()?.value == selectedFirstAttr
            }
            .map { it.getSecondVariantAttr()!!.value }
            .toSet()
            .toList()
    }

    fun getVariantByFirstAttr(product: Product): Variant? {
        return product.variants.firstOrNull {
            it.getFirstVariantAttr()?.value == selectedFirstAttr
        }
    }

    fun getVariantBySecondAttr(product: Product): Variant? {
        return product.variants.firstOrNull {
            it.getFirstVariantAttr()?.value == selectedFirstAttr
                    && it.getSecondVariantAttr()?.value == selectedSecondAttr
        }
    }

    fun askQuestion(productId: String, question: String) = viewModelScope.launch {
        _observeAskQuestion.postValue(DataState.Loading())
        productRepo.askQuestion(productId, question).collect {
            _observeAskQuestion.postValue(it)
        }
    }
}