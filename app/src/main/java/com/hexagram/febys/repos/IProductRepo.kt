package com.hexagram.febys.repos

import androidx.paging.PagingData
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun fetchProductDetail(
        productId: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Product>>
}