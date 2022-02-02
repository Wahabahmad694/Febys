package com.hexagram.febys.repos

import com.hexagram.febys.network.DataState
import com.hexagram.febys.models.api.febysPlusPackage.FebysPackageResponse
import com.hexagram.febys.models.api.febysPlusPackage.Package
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IFebysPlusRepo {
   suspend fun fetchFebysPackage(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<List<Package>>>
}