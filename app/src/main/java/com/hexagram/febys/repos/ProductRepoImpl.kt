package com.hexagram.febys.repos

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.onError
import com.hexagram.febys.network.adapter.onException
import com.hexagram.febys.network.adapter.onNetworkError
import com.hexagram.febys.network.adapter.onSuccess
import com.hexagram.febys.network.requests.RequestOfPagination
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
import com.hexagram.febys.paginations.TodayDealsPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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

    override fun fetchTodayDeals(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow<DataState<List<Product>>> {
            val req = mapOf("chunkSize" to 10, "pageNo" to 1)
            backendService.fetchTodayDeals(req)
                .onSuccess {
                    val products = data!!.getResponse<ResponseProductListing>().products
                    emit(DataState.Data(products))
                }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun fetchTodayDealsListing(
        scope: CoroutineScope,
        dispatcher: CoroutineDispatcher
    ): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 10)
        ) {
            TodayDealsPagingSource(backendService, RequestOfPagination())
        }.flow
            .flowOn(dispatcher)
            .cachedIn(scope)
    }

    override fun fetchTrendingProducts(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow<DataState<List<Product>>> {
            val req = mapOf("chunkSize" to 10, "pageNo" to 1)
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

    override fun fetchUnder100DollarsItems(dispatcher: CoroutineDispatcher): Flow<DataState<List<Product>>> {
        return flow<DataState<List<Product>>> {
            val req = mapOf("chunkSize" to 10, "pageNo" to 1)
            backendService.fetchUnder100DollarsItems(req)
                .onSuccess {
                    val products = data!!.getResponse<ResponseProductListing>().products
                    emit(DataState.Data(products))
                }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }
}