package com.hexagram.febys.repos

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.QAThread
import com.hexagram.febys.models.api.product.RatingAndReviews
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun fetchProductDetail(
        productId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Product>>

    suspend fun toggleFav(skuId: String)

    fun getFav(): MutableSet<String>

    suspend fun addToFav(skuId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun removeFromFav(skuId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun askQuestion(
        productId: String, question: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<MutableList<QAThread>>>

    suspend fun replyQuestion(
        productId: String,
        question: String,
        threadId: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<MutableList<QAThread>>>

    fun questionVoteUp(
        productId: String,
        threadId: String,
        revoke: Boolean,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<MutableList<QAThread>>>

    fun questionVoteDown(
        productId: String,
        threadId: String,
        revoke: Boolean,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<MutableList<QAThread>>>

    suspend fun fetchRecommendProducts(dispatcher: CoroutineDispatcher = Dispatchers.IO): List<Product>

    suspend fun reviewVoteUp(
        reviewId: String,
        revoke: Boolean,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<List<RatingAndReviews>>>

    suspend fun reviewVoteDown(
        reviewId: String,
        revoke: Boolean,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<List<RatingAndReviews>>>

    suspend fun fetchSimilarProducts(
        productId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): List<Product>
}