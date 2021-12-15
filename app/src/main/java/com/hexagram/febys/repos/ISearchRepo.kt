package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface ISearchRepo {
    fun fetchAllCategories(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Category>>

    fun fetchAllFilters(dispatcher: CoroutineDispatcher = Dispatchers.IO
    ):Flow<DataState<Filters>>
}