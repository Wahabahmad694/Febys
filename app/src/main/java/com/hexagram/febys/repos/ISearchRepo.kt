package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

interface ISearchRepo {
    fun fetchAllCategories(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Category>>
}