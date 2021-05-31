package com.android.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.android.febys.models.requests.RequestAllCategories
import com.android.febys.models.responses.Category
import com.android.febys.network.FebysService
import com.android.febys.repos.pagination.CategoryPagingSource
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class SearchRepo @Inject constructor(private val service: FebysService) {
    fun fetchTabs(): List<String> {
        return listOf("Categories", "Stores", "Vendor Stores", "Celebrity Market", "Promotions")
    }

    fun fetchAllCategories(scope: CoroutineScope): Flow<PagingData<Category>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            CategoryPagingSource(service, RequestAllCategories())
        }.flow
            .cachedIn(scope)
    }
}