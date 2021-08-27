package com.hexagram.febys.repos

import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.network.requests.RequestToggleFav
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseToggleFav
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

open class ProductRepoImpl @Inject constructor(
    val pref: IPrefManger,
    val backendService: FebysBackendService
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

    override suspend fun toggleFav(variantId: Int) {
        val addToFav = pref.toggleFav(variantId)

        val authToken = pref.getAccessToken()
        val req = RequestToggleFav(setOf(variantId))
        val response = if (addToFav) {
            backendService.addToWishList(authToken, req)
        } else {
            backendService.removeFromWishList(authToken, req)
        }

        updateFavList(response)
    }

    override fun getFav(): MutableSet<Int> = pref.getFav()

    override suspend fun addToFav(variantId: Int, dispatcher: CoroutineDispatcher) {
        val addToFav = pref.addToFav(variantId)

        if (addToFav) {
            val authToken = pref.getAccessToken()
            val req = RequestToggleFav(setOf(variantId))
            val response = backendService.addToWishList(authToken, req)

            updateFavList(response)
        }
    }

    override suspend fun removeFromFav(variantId: Int, dispatcher: CoroutineDispatcher) {
        val removeFromFav = pref.removeFromFav(variantId)

        if (removeFromFav) {
            val authToken = pref.getAccessToken()
            val req = RequestToggleFav(setOf(variantId))
            val response = backendService.removeFromWishList(authToken, req)

            updateFavList(response)
        }
    }

    private fun updateFavList(response: ApiResponse<ResponseToggleFav>) {
        if (response is ApiResponse.ApiSuccessResponse) {
            val fav = response.data!!.variantIds.toMutableSet()
            pref.saveFav(fav)
        }
    }
}