package com.hexagram.febys.repos

import com.hexagram.febys.models.view.Voucher
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FakeApiService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class VoucherRepoImpl @Inject constructor() : IVoucherRepo {
    override suspend fun fetchVouchers(dispatcher: CoroutineDispatcher) = flow<DataState<List<Voucher>>> {
        val response = FakeApiService.fetchVouchers()
        response.onSuccess {
            emit(DataState.Data(data!!))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}