package com.hexagram.febys.repos

import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FakeApiService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ShippingAddressRepoImpl @Inject constructor(
    private val pref: IPrefManger
) : IShippingAddressRepo {

    override suspend fun fetchShippingAddressRepo(
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<List<ShippingAddress>>> = flow<DataState<List<ShippingAddress>>> {
        val authToken = pref.getAccessToken()
        val response = FakeApiService.fetchShippingAddress(authToken)
        response.onSuccess {
            emit(DataState.Data(data!!))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun setAsDefault(id: Int) {
        FakeApiService.setAsDefault(id)
    }

    override suspend fun updateShippingAddress(
        shippingAddress: ShippingAddress, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> = flow<DataState<Unit>> {
        val authToken = pref.getAccessToken()
        val response = FakeApiService.updateShippingAddress(authToken, shippingAddress)
        response.onSuccess {
            emit(DataState.Data(Unit))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun addShippingAddress(
        shippingAddress: ShippingAddress, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> = flow<DataState<Unit>> {
        val authToken = pref.getAccessToken()
        val response = FakeApiService.addShippingAddress(authToken, shippingAddress)
        response.onSuccess {
            emit(DataState.Data(Unit))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}