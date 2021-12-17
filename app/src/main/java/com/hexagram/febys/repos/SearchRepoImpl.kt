package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.paginations.CategoryPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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
            CategoryPagingSource(service, PagingListRequest())
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }
}