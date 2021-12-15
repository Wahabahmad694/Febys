package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.paginations.CategoryPagingSource
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepoImpl @Inject constructor(
    private val service: FebysBackendService,
    private val pref: IPrefManger
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

    override fun fetchAllFilters(
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<Filters>> {
        val response = service.fetchFilters()
        response.onSuccess {
            emit(DataState.Data(data!!.filters))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}