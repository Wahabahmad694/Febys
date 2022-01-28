package com.hexagram.febys.repos

import com.google.gson.JsonObject
import com.hexagram.febys.models.api.vouchers.Voucher
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

class VoucherRepoImpl @Inject constructor(
    private val backendService: FebysBackendService,
    private val pref: IPrefManger
) : IVoucherRepo {
    override suspend fun fetchVouchers(dispatcher: CoroutineDispatcher) =
        flow<DataState<List<Voucher>>> {
            val authToken = pref.getAccessToken()
            if (authToken.isEmpty()) return@flow
            val filters = JsonObject()
            val sorter = JsonObject()
            sorter.addProperty("created_at", "desc")
            filters.add("sorter", sorter)
            val response = backendService.fetchVouchers(authToken, filters)
            response.onSuccess {
                emit(DataState.Data(data!!.vouchers))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)

    override fun collectVouchers(voucher: String, dispatcher: CoroutineDispatcher) =
        flow<DataState<Voucher>> {
            val authToken = pref.getAccessToken()
            if (authToken.isEmpty()) return@flow
            val response = backendService.collectVouchers(authToken, voucher)
            response.onSuccess {
                emit(DataState.Data(data!!.voucher))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
}