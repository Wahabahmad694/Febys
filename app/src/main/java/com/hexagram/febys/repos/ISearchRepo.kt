package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.api.category.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface ISearchRepo {
    fun fetchAllCategories(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Category>>
}