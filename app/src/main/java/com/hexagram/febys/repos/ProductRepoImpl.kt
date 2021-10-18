package com.hexagram.febys.repos

import com.hexagram.febys.models.api.product.FavSkuIds
import com.hexagram.febys.models.view.QuestionAnswersThread
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FakeApiService
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.network.response.OldProduct
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
    ) = flow<DataState<OldProduct>> {
        backendService.fetchProduct(productId)
            .onSuccess {
                emit(DataState.Data(data!!.oldProduct))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun toggleFav(skuId: String) {
        val addToFav = pref.toggleFav(skuId)

        val authToken = pref.getAccessToken()
        val req = FavSkuIds(setOf(skuId))
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
            val req = FavSkuIds(setOf(skuId))
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
            val req = FavSkuIds(setOf(skuId))
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
        productId: Int, question: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<QuestionAnswersThread>> {
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