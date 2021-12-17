package com.hexagram.febys.repos

import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IFiltersRepo {
    fun fetchAllFilters(
        filterType: FiltersType,
        categoryId: String?,
        vendorId: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Filters>>
}