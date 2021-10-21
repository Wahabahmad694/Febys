package com.hexagram.febys.repos

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.QuestionAnswers
import com.hexagram.febys.models.api.wishlist.FavSkuIds
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FakeApiService
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
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
        productId: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<Product>> {
        backendService.fetchProduct(productId)
            .onSuccess {
                emit(DataState.Data(data!!.product))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun toggleFav(skuId: String) {
        val addToFav = pref.toggleFav(skuId)

        val authToken = pref.getAccessToken()
        val req = FavSkuIds(mutableSetOf(skuId))
        if (addToFav) {
            val response = backendService.addToWishList(authToken, req)
            if (response is ApiResponse.ApiSuccessResponse) {
                updateFavList(response.data!!.wishList.skuIds)
            }
        } else {
            val response = backendService.removeFromWishList(authToken, req)
            if (response is ApiResponse.ApiSuccessResponse) {
                updateFavList(response.data!!.skuIds)
            }
        }
    }

    override fun getFav(): MutableSet<String> = pref.getFav()

    override suspend fun addToFav(skuId: String, dispatcher: CoroutineDispatcher) {
        val addToFav = pref.addToFav(skuId)

        if (addToFav) {
            val authToken = pref.getAccessToken()
            val req = FavSkuIds(mutableSetOf(skuId))
            val response = backendService.addToWishList(authToken, req)

            if (response is ApiResponse.ApiSuccessResponse) {
                updateFavList(response.data!!.wishList.skuIds)
            }
        }
    }

    override suspend fun removeFromFav(skuId: String, dispatcher: CoroutineDispatcher) {
        val removeFromFav = pref.removeFromFav(skuId)

        if (removeFromFav) {
            val authToken = pref.getAccessToken()
            val req = FavSkuIds(mutableSetOf(skuId))
            val response = backendService.removeFromWishList(authToken, req)
            if (response is ApiResponse.ApiSuccessResponse) {
                updateFavList(response.data!!.skuIds)
            }
        }
    }

    private fun updateFavList(skuIds: Set<String>) {
        val fav = skuIds.toMutableSet()
        pref.saveFav(fav)
    }

    override suspend fun askQuestion(
        productId: String, question: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<QuestionAnswers>> {
        val authToken = pref.getAccessToken()
        FakeApiService.postQuestion(authToken, productId, question)
            .onSuccess {
                emit(DataState.Data(data!!))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}