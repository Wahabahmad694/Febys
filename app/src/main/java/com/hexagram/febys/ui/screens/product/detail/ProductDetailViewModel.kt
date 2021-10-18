package com.hexagram.febys.ui.screens.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.view.QuestionAnswersThread
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.OldProduct
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

    private val _observeProductDetail = MutableLiveData<DataState<OldProduct>>()
    val observeOldProductDetail: LiveData<DataState<OldProduct>> = _observeProductDetail

    private val _observeAskQuestion = MutableLiveData<DataState<QuestionAnswersThread>>()
    val observeAskQuestion: LiveData<DataState<QuestionAnswersThread>> = _observeAskQuestion

    fun fetchProductDetail(productId: Int) = viewModelScope.launch {
        _observeProductDetail.postValue(DataState.Loading())
        productRepo.fetchProductDetail(productId).collect {
            _observeProductDetail.postValue(it)
        }
    }

    fun getFirstAttrList(oldProduct: OldProduct): List<String> {
        return oldProduct.productVariants
            .filter { it.getFirstVariantAttr()?.value != null }
            .map { it.getFirstVariantAttr()!!.value }
            .toSet()
            .toList()
    }

    fun getSecondAttrList(selectedFirstAttr: String, oldProduct: OldProduct): List<String> {
        return oldProduct.productVariants
            .filter {
                it.getSecondVariantAttr()?.value != null && it.getFirstVariantAttr()?.value == selectedFirstAttr
            }
            .map { it.getSecondVariantAttr()!!.value }
            .toSet()
            .toList()
    }

    fun getVariantByFirstAttr(oldProduct: OldProduct): ProductVariant? {
        return oldProduct.productVariants.firstOrNull {
            it.getFirstVariantAttr()?.value == selectedFirstAttr
        }
    }

    fun getVariantBySecondAttr(oldProduct: OldProduct): ProductVariant? {
        return oldProduct.productVariants.firstOrNull {
            it.getFirstVariantAttr()?.value == selectedFirstAttr
                    && it.getSecondVariantAttr()?.value == selectedSecondAttr
        }
    }

    fun askQuestion(productId: Int, question: String) = viewModelScope.launch {
        _observeAskQuestion.postValue(DataState.Loading())
        productRepo.askQuestion(productId, question).collect {
            _observeAskQuestion.postValue(it)
        }
    }
}