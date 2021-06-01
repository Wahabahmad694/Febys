package com.android.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.febys.network.requests.RequestAllCategories
import com.android.febys.network.response.Category
import com.android.febys.network.FebysBackendService
import com.android.febys.paginations.CategoryPagingSource
import com.android.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SearchRepoImpl(
    private val service: FebysBackendService
) : ISearchRepo {

    override fun fetchTabs(dispatcher: CoroutineDispatcher): Flow<DataState<List<String>>> {
        return flow {
            val list =
                listOf("Categories", "Stores", "Vendor Stores", "Celebrity Market", "Promotions")

            emit(DataState.Data(list))
        }.flowOn(dispatcher)
    }

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