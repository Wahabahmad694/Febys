package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.models.view.VendorListing
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
}