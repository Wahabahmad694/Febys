package com.hexagram.febys.repos

import com.hexagram.febys.models.view.QuestionAnswersThread
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun fetchProductDetail(
        productId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Product>>

    suspend fun toggleFav(variantId: Int)

    fun getFav(): MutableSet<Int>

    suspend fun addToFav(variantId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun removeFromFav(variantId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun askQuestion(
        productId: Int, question: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<QuestionAnswersThread>>
}