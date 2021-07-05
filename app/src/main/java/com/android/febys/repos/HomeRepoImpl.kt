package com.android.febys.repos

import com.android.febys.R
import com.android.febys.network.DataState
import com.android.febys.network.FebysBackendService
import com.android.febys.network.FebysWebCustomizationService
import com.android.febys.network.adapter.onError
import com.android.febys.network.adapter.onException
import com.android.febys.network.adapter.onNetworkError
import com.android.febys.network.adapter.onSuccess
import com.android.febys.network.response.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepoImpl @Inject constructor(
    private val webCustomizationService: FebysWebCustomizationService,
    private val backendService: FebysBackendService
) : IHomeRepo {

    override fun fetchAllUniqueCategories(dispatcher: CoroutineDispatcher): Flow<DataState<List<UniqueCategory>>> {
        return flow<DataState<List<UniqueCategory>>> {
            webCustomizationService.fetchAllUniqueCategories()
                .onSuccess { emit(DataState.Data(data!!)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun fetchAllBanner(dispatcher: CoroutineDispatcher): Flow<DataState<List<Banner>>> {
        return flow<DataState<List<Banner>>> {
            webCustomizationService.fetchAllBanner()
                .onSuccess { emit(DataState.Data(data!!)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

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

    override fun fetchFeaturedCategories(dispatcher: CoroutineDispatcher): Flow<DataState<List<Category>>> {
        return flow<DataState<List<Category>>> {
            backendService.fetchFeaturedCategories()
                .onSuccess {
                    emit(DataState.Data(data!!))
                }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun fetchAllSeasonalOffers(dispatcher: CoroutineDispatcher): Flow<DataState<List<SeasonalOffer>>> {
        return flow<DataState<List<SeasonalOffer>>> {
            webCustomizationService.fetchAllSeasonalOffers()
                .onSuccess { emit(DataState.Data(data!!)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
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

    override fun fetchStoresYouFollow(dispatcher: CoroutineDispatcher): Flow<DataState<List<String>>> {
        return flow {
            val list = listOf(
                "res:///${R.drawable.ic_shirt}",
                "res:///${R.drawable.ic_shirt}"
            )
            emit(DataState.Data(list))
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

    private fun getProductList(): List<Product> {
        return listOf()
    }
}