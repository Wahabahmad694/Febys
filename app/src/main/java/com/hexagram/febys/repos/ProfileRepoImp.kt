package com.hexagram.febys.repos

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileRepoImp @Inject constructor(
    private val backendService: FebysBackendService,
    private val pref: IPrefManger
) : IProfileRepo {
    override suspend fun fetchProfile(dispatcher: CoroutineDispatcher) =
        flow<DataState<Consumer>> {
            val authToken = pref.getAccessToken()
            if (authToken.isEmpty()) return@flow
            val response = backendService.fetchProfile(authToken)
            response.onSuccess {
                emit(DataState.Data(data!!.consumerInfo))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
}