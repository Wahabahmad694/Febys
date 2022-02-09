package com.hexagram.febys.repos


import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.models.api.transaction.TransactionReq
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

class FebysPlusRepoImpl @Inject constructor(
    private val backendService: FebysBackendService,
    private val pref: IPrefManger,
) : IFebysPlusRepo {
    override suspend fun fetchFebysPackage(dispatcher: CoroutineDispatcher) =
        flow<DataState<List<Package>>> {
            val response = backendService.fetchFebysPlusPackage()
            response.onSuccess {
                emit(DataState.Data(data!!.packages))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)

    override suspend fun subscribePackage(
        packageId: String, transaction: TransactionReq, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Unit>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow
        backendService.subscribePackage(authToken, packageId, transaction)
            .onSuccess { emit(DataState.Data(data!!)) }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }
}