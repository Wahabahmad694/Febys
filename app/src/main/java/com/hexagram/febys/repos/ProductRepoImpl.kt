package com.hexagram.febys.repos

import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.models.api.product.QAThread
import com.hexagram.febys.models.api.product.RatingAndReviews
import com.hexagram.febys.models.api.request.AskQuestionRequest
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.request.ReplyQuestionRequest
import com.hexagram.febys.models.api.wishlist.FavSkuIds
import com.hexagram.febys.network.DataState
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
    ) = flow<DataState<MutableList<QAThread>>> {
        val authToken = pref.getAccessToken()
        backendService.askQuestion(authToken, productId, AskQuestionRequest(question))
            .onSuccess {
                emit(DataState.Data(data!!.questionAnswers.qaThreads))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun replyQuestion(
        productId: String, question: String, threadId: String, dispatcher: CoroutineDispatcher
    ) = flow<DataState<MutableList<QAThread>>> {
        val authToken = pref.getAccessToken()
        backendService.replyQuestion(authToken, productId, ReplyQuestionRequest(threadId, question))
            .onSuccess {
                emit(DataState.Data(data!!.questionAnswers.qaThreads))
            }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override fun questionVoteUp(
        productId: String, threadId: String, revoke: Boolean, dispatcher: CoroutineDispatcher
    ) = flow<DataState<MutableList<QAThread>>> {
        val authToken = pref.getAccessToken()

        val response = if (!revoke)
            backendService.questionVoteUp(authToken, productId, threadId)
        else
            backendService.revokeQuestionVoteUp(authToken, productId, threadId)

        response.onSuccess {
            emit(DataState.Data(data!!.questionAnswers.qaThreads))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override fun questionVoteDown(
        productId: String, threadId: String, revoke: Boolean, dispatcher: CoroutineDispatcher
    ) = flow<DataState<MutableList<QAThread>>> {
        val authToken = pref.getAccessToken()

        val response = if (!revoke)
            backendService.questionVoteDown(authToken, productId, threadId)
        else
            backendService.revokeQuestionVoteDown(authToken, productId, threadId)

        response.onSuccess {
            emit(DataState.Data(data!!.questionAnswers.qaThreads))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun fetchRecommendProducts(
        productId: String, dispatcher: CoroutineDispatcher
    ): List<Product> {
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchRecommendProducts(
            pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun fetchSimilarProducts(
        productId: String, dispatcher: CoroutineDispatcher
    ): List<Product> {
        val pagingListRequest = PagingListRequest()
        val response = backendService.fetchSimilarProducts(
            productId, pagingListRequest.createQueryMap(), pagingListRequest
        )
        return (response as? ApiResponse.ApiSuccessResponse)
            ?.data?.getResponse<ProductPagingListing>()?.products ?: emptyList()
    }

    override suspend fun reviewVoteUp(
        reviewId: String,
        revoke: Boolean,
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<List<RatingAndReviews>>> {
        val authToken = pref.getAccessToken()

        val response = if (!revoke)
            backendService.reviewVoteUp(authToken, reviewId)
        else
            backendService.revokeReviewVoteUp(authToken, reviewId)

        response.onSuccess {
            emit(DataState.Data(data!!.ratingAndReviews))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)

    override suspend fun reviewVoteDown(
        reviewId: String,
        revoke: Boolean,
        dispatcher: CoroutineDispatcher
    ) = flow<DataState<List<RatingAndReviews>>> {
        val authToken = pref.getAccessToken()

        val response = if (!revoke)
            backendService.reviewVoteDown(authToken, reviewId)
        else
            backendService.revokeReviewVoteDown(authToken, reviewId)

        response.onSuccess {
            emit(DataState.Data(data!!.ratingAndReviews))
        }
            .onError { emit(DataState.ApiError(message)) }
            .onException { emit(DataState.ExceptionError()) }
            .onNetworkError { emit(DataState.NetworkError()) }
    }.flowOn(dispatcher)
}