package com.android.febys.repos

import com.android.febys.network.DataState
import com.android.febys.network.FebysBackendService
import com.android.febys.network.adapter.onError
import com.android.febys.network.adapter.onException
import com.android.febys.network.adapter.onNetworkError
import com.android.febys.network.adapter.onSuccess
import com.android.febys.network.response.Product
import com.android.febys.network.response.ResponseProductListing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductRepoImpl @Inject constructor(
    private val backendService: FebysBackendService
) : IProductRepo {
    override fun fetchProductDetail(
        productId: Int, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Product>> {
        backendService.fetchProduct(productId)
            .onSuccess {
                emit(DataState.Data(data!!.product))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override fun fetchWishList(dispatcher: CoroutineDispatcher) = flow<DataState<List<Product>>> {
        val req = mapOf("chunkSize" to 10, "pageNo" to 1)
        // todo change this when api end point available
        backendService.fetchTrendingProducts(req)
            .onSuccess {
                val products = data!!.getResponse<ResponseProductListing>().products
                emit(DataState.Data(products))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}