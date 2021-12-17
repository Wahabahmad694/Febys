package com.hexagram.febys.repos

import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FiltersRepoImpl @Inject constructor(val backendService: FebysBackendService) : IFiltersRepo {
    override fun fetchAllFilters(
        filterType: FiltersType,
        categoryId: String?,
        vendorId: String?,
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<Filters>> {
        val response = when (filterType) {
            FiltersType.SEARCH -> backendService.fetchSearchFilters()
            FiltersType.CATEGORY -> backendService.fetchCategoryFilters(categoryId!!)
            FiltersType.TODAY_DEALS -> backendService.fetchTodayDealsFilters()
            FiltersType.VENDOR_CATEGORY -> backendService.fetchVendorCategoryFilters(vendorId!!)
        }

        response.onSuccess {
            emit(DataState.Data(data!!.filters))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}