package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.api.vendor.Endorsement
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IVendorRepo {
    fun fetchVendors(
        isCelebrity: Boolean,
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<PagingData<Any>>

    suspend fun followVendor(vendorId: String)

    suspend fun unFollowVendor(vendorId: String)

    fun fetchVendorDetail(
        vendorId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Vendor>>

    fun fetchVendorEndorsement(
        vendorId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<List<Endorsement>>>
}