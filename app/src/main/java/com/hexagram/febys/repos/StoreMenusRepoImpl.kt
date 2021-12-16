package com.hexagram.febys.repos

import com.hexagram.febys.models.api.menu.StoreMenus
import com.hexagram.febys.models.api.menu.StoreTemplate
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysWebCustomizationService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StoreMenusRepoImpl @Inject constructor(
    private val backendWebService: FebysWebCustomizationService
) : IStoreMenusRepo {
    override suspend fun fetchAllMenu(
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<List<StoreMenus>>> {
        backendWebService.fetchAllMenu()
            .onSuccess {
                emit(DataState.Data(data!![0].navigationMenu))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }

    override suspend fun fetchStoreTemplate(
        storeId: String,
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<StoreTemplate>> {
        backendWebService.fetchStoreTemplate(storeId)
            .onSuccess {
                emit(DataState.Data(data!!))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }
}