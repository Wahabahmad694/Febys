package com.hexagram.febys.repos

import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
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