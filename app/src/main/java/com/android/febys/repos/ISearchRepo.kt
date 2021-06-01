package com.android.febys.repos

import androidx.paging.PagingData
import com.android.febys.network.response.Category
import com.android.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface ISearchRepo {
    fun fetchTabs(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<String>>>

    fun fetchAllCategories(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Category>>
}