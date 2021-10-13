package com.hexagram.febys.repos

import com.hexagram.febys.models.view.Voucher
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IVoucherRepo {
    suspend fun fetchVouchers(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Voucher>>>
}