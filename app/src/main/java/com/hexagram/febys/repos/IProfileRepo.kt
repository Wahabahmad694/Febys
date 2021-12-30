package com.hexagram.febys.repos

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProfileRepo {
    suspend fun fetchProfile(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<Consumer>>
}