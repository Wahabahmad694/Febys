package com.hexagram.febys.repos

import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FiltersRepoImpl @Inject constructor(val backendService: FebysBackendService) : IFiltersRepo {
    override fun fetchAllFilters(
        filterType: FiltersType,
        categoryId: Int?,
        vendorId: String?,
        searchStr: String?,
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<Filters>> {
        val response = when (filterType) {
            FiltersType.SEARCH -> backendService.fetchSearchFilters(searchStr ?: "")
            FiltersType.CATEGORY -> backendService.fetchCategoryFilters(categoryId!!)
            FiltersType.TODAY_DEALS -> backendService.fetchTodayDealsFilters()
            FiltersType.VENDOR -> backendService.fetchVendorFilters(vendorId!!)
            FiltersType.UNDER_HUNDRED -> backendService.fetchUnderHundredFilters()
            FiltersType.RECOMMENDED_PRODUCT,
            FiltersType.SIMILAR_PRODUCT,
            FiltersType.SPECIAL_PRODUCT,
            FiltersType.TRENDING,
            FiltersType.EDITORS_PICK,
            FiltersType.SAME_DAY_DELIVERY,
            FiltersType.STORE_YOU_FOLLOW -> {
                ApiResponse.exception(NullPointerException())
            }
        }

        response.onSuccess {
            emit(DataState.Data(data!!.filters))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}