package com.hexagram.febys.repos

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.QuestionAnswers
import com.hexagram.febys.models.view.QuestionAnswersThread
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.OldProduct
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
    ): Flow<DataState<QuestionAnswers>>
}