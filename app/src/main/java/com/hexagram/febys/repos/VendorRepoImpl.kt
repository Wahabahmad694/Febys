package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.models.view.VendorListing
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.paginations.VendorListingPagingSource
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class VendorRepoImpl @Inject constructor(
    private val service: FebysBackendService,
    private val pref: IPrefManger
) : IVendorRepo {
    override fun fetchVendors(
        isCelebrity: Boolean, scope: CoroutineScope, dispatcher: CoroutineDispatcher
    ): Flow<PagingData<VendorListing>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            val authKey = pref.getAccessToken()
            VendorListingPagingSource(authKey, service, isCelebrity, RequestOfPagination())
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }
}