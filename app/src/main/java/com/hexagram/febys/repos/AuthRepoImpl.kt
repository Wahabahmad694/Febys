package com.hexagram.febys.repos

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.hexagram.febys.dataSource.IUserDataSource
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.profile.Profile
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.subscription.Subscription
import com.hexagram.febys.network.AuthService
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.ResponseLogin
import com.hexagram.febys.network.response.ResponseOtpVerification
import com.hexagram.febys.network.response.ResponseSignup
import com.hexagram.febys.network.response.User
import com.hexagram.febys.notification.FCMService
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.ui.screens.payment.models.Wallet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService,
    private val pref: IPrefManger,
    private val userDataSource: IUserDataSource,
    private val cartRepo: ICartRepo
) : IAuthRepo {

    override fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseSignup>> {
        return flow<DataState<ResponseSignup>> {
            val response = authService.signup(signupReq)
            response.onSuccess {
                data!!.apply {
                    saveUserAndToken(user)
                    fetchUserProfile(dispatcher)
                    emit(DataState.Data(this))
                }
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun verifyUser(
        otp: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseOtpVerification>> {
        return flow<DataState<ResponseOtpVerification>> {
            val authToken = pref.getAccessToken()
            val verificationReq = mapOf("otp" to otp)
            val response = authService.verifyUser(authToken, verificationReq)
            response.onSuccess {
                emit(DataState.Data(data!!))
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun login(
        email: String, password: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseLogin>> {
        return flow<DataState<ResponseLogin>> {
            val loginReq = mapOf("email" to email, "password" to password)
            authService.login(loginReq).onSuccess {
                data!!.apply {
                    saveUserAndToken(user)
                    fetchUserProfile(dispatcher)
                    emit(DataState.Data(data))
                }
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun resetCredentials(
        email: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> {
        return flow<DataState<Unit>> {
            val resetCredentialReq = mapOf("email" to email)
            authService.resetCredentials(resetCredentialReq)
                .onSuccess { emit(DataState.Data(Unit)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun socialLogin(
        token: String, socialLogin: SocialLogin, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseLogin>> {
        return flow<DataState<ResponseLogin>> {
            val socialLoginReq = mapOf(
                "access_token" to token,
                "type" to socialLogin.name.lowercase(),
                "client" to "android"
            )

            authService.socialLogin(socialLoginReq).onSuccess {
                data!!.apply {
                    saveUserAndToken(user)
                    fetchUserProfile(dispatcher)
                    emit(DataState.Data(this))
                }
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    private fun saveUserAndToken(user: User) {
        userDataSource.saveUser(user)
        userDataSource.saveAccessToken(user.accessToken ?: "")
        userDataSource.saveRefreshToken(user.refreshToken ?: "")
    }

    override suspend fun fetchUserProfile(dispatcher: CoroutineDispatcher): Flow<DataState<Profile?>> {
        val authKey = pref.getAccessToken()
        val response = authService.me(authKey)
        if (response is ApiResponse.ApiSuccessResponse) {
            val profile = response.data!!
            userDataSource.saveConsumer(profile.consumerInfo)
            updateWishlist(profile.wishlist.skuIds.toMutableSet())
            updateShippingAddress(profile.shippingAddresses)
            updateCart(profile.cart)
            updateWallet(profile.wallet)
            saveSubscription(profile.subscription)

            subscribeToTopicForNotification(profile.consumerInfo.id.toString())
        }

        return flow<DataState<Profile?>> {
            if (authKey.isEmpty()) {
                emit(DataState.Data(null))
                return@flow
            }
            response
                .onSuccess { emit(DataState.Data(data!!)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    private fun subscribeToTopicForNotification(topic: String) {
        Firebase.messaging.subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Notification subscribed to topic: $topic"
                if (!task.isSuccessful) {
                    msg = "Notification subscription failed"
                }
                Log.d(FCMService.TAG, msg)
            }
    }

    private fun saveSubscription(subscription: Subscription?) {
        if (subscription != null) {
            pref.saveSubscription(subscription)
        }
    }

    private suspend fun updateCart(cart: Cart) {
        cartRepo.updateCart(cart)
    }

    private fun updateWallet(wallet: Wallet) {
        pref.saveWallet(wallet)
    }

    private fun updateWishlist(skuIds: MutableSet<String>) {
        pref.saveFav(skuIds)
    }

    private fun updateShippingAddress(shippingAddresses: List<ShippingAddress>) {
        val defaultShippingAddress = shippingAddresses.firstOrNull { it.shippingDetail.isDefault }
        if (defaultShippingAddress != null) {
            pref.saveDefaultShippingAddress(defaultShippingAddress)
        }
    }

    override fun signOut() {
        val topic = pref.getConsumer()?.id.toString()
        Firebase.messaging.unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                var msg = "Notification un-subscribed for topic: $topic"
                if (!task.isSuccessful) {
                    msg = "Notification un-subscribe failed"
                }
                Log.d(FCMService.TAG, msg)
            }
        IAuthRepo.signOut(pref, cartRepo)
    }

    override fun getUser(): User? {
        return userDataSource.getUser()
    }

    override fun getConsumer(): Consumer? {
        return userDataSource.getConsumer()
    }

    override fun getWallet(): Wallet? {
        return pref.getWallet()
    }

    override fun getSubscription(): Subscription? {
        return pref.getSubscription()
    }
}