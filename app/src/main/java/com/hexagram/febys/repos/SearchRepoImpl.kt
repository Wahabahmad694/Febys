package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.network.requests.RequestAllCategories
import com.hexagram.febys.network.response.Category
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.paginations.CategoryPagingSource
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepoImpl @Inject constructor(
    private val service: FebysBackendService
) : ISearchRepo {
    override fun fetchAllCategories(
        scope: CoroutineScope, dispatcher: CoroutineDispatcher
    ): Flow<PagingData<Category>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            CategoryPagingSource(service, RequestAllCategories())
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }
}