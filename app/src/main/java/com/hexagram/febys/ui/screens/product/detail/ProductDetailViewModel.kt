package com.hexagram.febys.ui.screens.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.QAThread
import com.hexagram.febys.models.api.product.RatingAndReviews
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
    val observeProductDetail: LiveData<DataState<Product>> = _observeProductDetail

    private val _observeQAThreads = MutableLiveData<DataState<MutableList<QAThread>>>()
    val observeQAThreads: LiveData<DataState<MutableList<QAThread>>> = _observeQAThreads

    private val _observeReviews = MutableLiveData<DataState<List<RatingAndReviews>>>()
    val observeReviews: LiveData<DataState<List<RatingAndReviews>>> = _observeReviews

    private val _recommendProducts = MutableLiveData<DataState<List<Product>>>()
    val recommendProducts: LiveData<DataState<List<Product>>> = _recommendProducts

    private val _similarProducts = MutableLiveData<DataState<List<Product>>>()
    val similarProducts: LiveData<DataState<List<Product>>> = _similarProducts

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
        this@ProductDetailViewModel._observeQAThreads.postValue(DataState.Loading())
        productRepo.askQuestion(productId, question).collect {
            if (it is DataState.Data) updateQAThreads(it.data)
            this@ProductDetailViewModel._observeQAThreads.postValue(it)
        }
    }

    fun replyQuestion(productId: String, answer: String, threadId: String) =
        viewModelScope.launch {
            this@ProductDetailViewModel._observeQAThreads.postValue(DataState.Loading())
            productRepo.replyQuestion(productId, answer, threadId).collect {
                if (it is DataState.Data) updateQAThreads(it.data)
                this@ProductDetailViewModel._observeQAThreads.postValue(it)
            }
        }

    fun questionsVoteUp(productId: String, threadId: String, revoke: Boolean) =
        viewModelScope.launch {
            this@ProductDetailViewModel._observeQAThreads.postValue(DataState.Loading())
            productRepo.questionVoteUp(productId, threadId, revoke).collect {
                if (it is DataState.Data) updateQAThreads(it.data)
                this@ProductDetailViewModel._observeQAThreads.postValue(it)
            }
        }

    fun questionVoteDown(productId: String, threadId: String, revoke: Boolean) =
        viewModelScope.launch {
            this@ProductDetailViewModel._observeQAThreads.postValue(DataState.Loading())
            productRepo.questionVoteDown(productId, threadId, revoke).collect {
                if (it is DataState.Data) updateQAThreads(it.data)
                this@ProductDetailViewModel._observeQAThreads.postValue(it)
            }
        }

    fun updateQAThreads(qaThread: MutableList<QAThread>) {
        val productState = _observeProductDetail.value
        if (productState is DataState.Data) {
            productState.data._qaThreads = qaThread.asReversed()
        }
    }

    fun fetchRecommendProducts(productId: String) = viewModelScope.launch {
        val products = productRepo.fetchRecommendProducts(productId)
        _recommendProducts.postValue(DataState.Data(products))
    }

    fun fetchSimilarProducts(productId: String) = viewModelScope.launch {
        val products = productRepo.fetchSimilarProducts(productId)
        _similarProducts.postValue(DataState.Data(products))
    }

    fun getRecommendProducts() = (_recommendProducts.value as? DataState.Data<List<Product>>)?.data

    fun getSimilarProducts() = (_similarProducts.value as? DataState.Data<List<Product>>)?.data

    fun reviewVoteUp(reviewId: String, revoke: Boolean) = viewModelScope.launch {
        this@ProductDetailViewModel._observeReviews.postValue(DataState.Loading())
        productRepo.reviewVoteUp(reviewId, revoke).collect {
            if (it is DataState.Data) updateReviews(it.data)
            this@ProductDetailViewModel._observeReviews.postValue(it)
        }
    }

    fun reviewVoteDown(reviewId: String, revoke: Boolean) = viewModelScope.launch {
        this@ProductDetailViewModel._observeReviews.postValue(DataState.Loading())
        productRepo.reviewVoteDown(reviewId, revoke).collect {
            if (it is DataState.Data) updateReviews(it.data)
            this@ProductDetailViewModel._observeReviews.postValue(it)
        }
    }

    fun updateReviews(reviews: List<RatingAndReviews>) {
        val productState = _observeProductDetail.value
        if (productState is DataState.Data) {
            productState.data.ratingsAndReviews = reviews
        }
    }

    fun reviewsByMostRecent(ratingsAndReviews: List<RatingAndReviews>) =
        ratingsAndReviews.asReversed()

    fun reviewsByRating(ratingsAndReviews: List<RatingAndReviews>, asc: Boolean = false) =
        if (asc) ratingsAndReviews.sortedBy { it.score } else ratingsAndReviews.sortedByDescending { it.score }
}