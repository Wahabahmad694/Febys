package com.android.febys.repos

import com.android.febys.dto.ProductDetail
import com.android.febys.network.DataState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IProductRepo {
    fun fetchProductDetail(
        productId: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ProductDetail>>
}