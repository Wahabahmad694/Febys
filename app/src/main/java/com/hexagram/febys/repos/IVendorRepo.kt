package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.view.VendorDetail
import com.hexagram.febys.models.view.VendorListing
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
    ): Flow<PagingData<VendorListing>>

    suspend fun followVendor(vendorId: Int)

    suspend fun unFollowVendor(vendorId: Int)

    fun fetchVendorDetail(
        vendorId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<VendorDetail>>
}