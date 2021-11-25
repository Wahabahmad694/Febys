package com.hexagram.febys.repos

import com.hexagram.febys.models.api.cities.PostCitiesResponse
import com.hexagram.febys.models.api.countries.CountryResponse
import com.hexagram.febys.models.api.request.GetCitiesRequest
import com.hexagram.febys.models.api.request.GetStatesRequest
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.states.PostStatesResponse
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ShippingAddressRepoImpl @Inject constructor(
    private val backendService: FebysBackendService,
    private val pref: IPrefManger

) : IShippingAddressRepo {

    override suspend fun fetchShippingAddressRepo(
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<List<ShippingAddress>>> = flow<DataState<List<ShippingAddress>>> {
        val authToken = pref.getAccessToken()
        if (authToken.isEmpty()) return@flow
        val response = backendService.fetchShippingAddress(authToken)
        response.onSuccess {
            val shippingAddresses = sortShippingAddresses(data!!.shippingDetails)
            emit(DataState.Data(shippingAddresses))
            val defaultShippingAddress = data.shippingDetails.firstOrNull {
                it.shippingDetail.isDefault
            }
            defaultShippingAddress?.let { saveDefaultShippingAddress(it) }
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun addEditShippingAddress(
        shippingAddress: ShippingAddress, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> = flow<DataState<Unit>> {
        val authToken = pref.getAccessToken()
        val response = backendService.addEditShippingAddress(authToken, shippingAddress)
        response
            .onSuccess {
                emit(DataState.Data(Unit))
                if (shippingAddress.shippingDetail.isDefault) {
                    saveDefaultShippingAddress(shippingAddress)
                }
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun deleteShippingAddress(
        shippingAddress: ShippingAddress,
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> = flow<DataState<Unit>> {
        val authToken = getAuthToken()
        if (authToken.isEmpty()) return@flow
        shippingAddress.id?.let {
            backendService.removeShippingAddress(authToken, it)
                .onSuccess {
                    emit(DataState.Data(Unit))
                    if (shippingAddress.shippingDetail.isDefault) {
                        pref.removeShippingAddress()
                    }
                }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }

    }.flowOn(dispatcher)


    override suspend fun fetchCountries(
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<CountryResponse>> = flow<DataState<CountryResponse>> {
        val authToken = getAuthToken()
        if (authToken.isEmpty()) return@flow
        backendService.fetchCountries(authToken)
            .onSuccess {
                emit(DataState.Data(data!!))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }

    }.flowOn(dispatcher)

    override suspend fun getStates(
        getStatesRequest: GetStatesRequest,
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<PostStatesResponse>> = flow<DataState<PostStatesResponse>> {
        val authToken = getAuthToken()
        if (authToken.isEmpty()) return@flow
        backendService.getStates(authToken, getStatesRequest)
            .onSuccess {
                emit(DataState.Data(data!!))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun getCities(
        getCitiesRequest: GetCitiesRequest,
        dispatcher: CoroutineDispatcher
    ): Flow<DataState<PostCitiesResponse>> = flow<DataState<PostCitiesResponse>> {
        val authToken = getAuthToken()
        if (authToken.isEmpty()) return@flow
        backendService.getCities(authToken, getCitiesRequest)
            .onSuccess {
                emit(DataState.Data(data!!))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    private fun sortShippingAddresses(shippingAddresses: List<ShippingAddress>): List<ShippingAddress> {
        val mutableShippingAddresses = shippingAddresses.toMutableList()
        val defaultIndex = mutableShippingAddresses.indexOfFirst { it.shippingDetail.isDefault }
        if (defaultIndex != -1) {
            val defaultShippingAddress = mutableShippingAddresses.removeAt(defaultIndex)
            mutableShippingAddresses.add(0, defaultShippingAddress)
        }

        return mutableShippingAddresses
    }

    private fun saveDefaultShippingAddress(shippingAddress: ShippingAddress) {
        pref.saveDefaultShippingAddress(shippingAddress)
    }

    private fun getAuthToken(): String {
        return pref.getAccessToken()
    }
}