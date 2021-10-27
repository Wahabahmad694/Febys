package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.paginations.VendorListingPagingSource
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class VendorRepoImpl @Inject constructor(
    private val backendService: FebysBackendService,
    private val pref: IPrefManger
) : IVendorRepo {
    override fun fetchVendors(
        isCelebrity: Boolean, scope: CoroutineScope, dispatcher: CoroutineDispatcher
    ): Flow<PagingData<Any>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authKey = getAuthKey()
            VendorListingPagingSource(authKey, backendService, isCelebrity, PagingListRequest())
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override suspend fun followVendor(vendorId: String) {
        val authKey = getAuthKey()
        val response = backendService.followVendor(authKey, vendorId)
        response.onSuccess {
            // do nothing
        }
    }

    override suspend fun unFollowVendor(vendorId: String) {
        val authKey = getAuthKey()
        val response = backendService.unFollowVendor(authKey, vendorId)
        response.onSuccess {
            // do nothing
        }
    }

    override fun fetchVendorDetail(
        vendorId: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Vendor>> = flow<DataState<Vendor>> {
        val response = backendService.fetchVendorDetail(vendorId)
        response.onSuccess {
            emit(DataState.Data(data!!))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    private fun getAuthKey(): String = pref.getAccessToken()
}