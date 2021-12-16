package com.hexagram.febys.repos

import com.hexagram.febys.models.api.menu.StoreMenus
import com.hexagram.febys.models.api.menu.StoreTemplate
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IStoreMenusRepo {
    suspend fun fetchAllMenu(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<StoreMenus>>>
    suspend fun fetchStoreTemplate(
        storeId: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<StoreTemplate>>
}