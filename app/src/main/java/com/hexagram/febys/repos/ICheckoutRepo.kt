package com.hexagram.febys.repos

import com.hexagram.febys.models.view.CheckoutModel
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface ICheckoutRepo {
    suspend fun fetchCheckoutModel(
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<CheckoutModel>>
}