package com.hexagram.febys.repos

import com.hexagram.febys.models.api.transaction.TransactionReq
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IFebysPlusRepo {
    suspend fun fetchFebysPackage(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Package>>>

    suspend fun subscribePackage(
        packageId: String,
        transaction: TransactionReq,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Unit>>
}