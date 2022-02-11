package com.hexagram.febys.repos

import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.profile.Profile
import com.hexagram.febys.models.api.subscription.Subscription
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.ResponseLogin
import com.hexagram.febys.network.response.ResponseOtpVerification
import com.hexagram.febys.network.response.ResponseSignup
import com.hexagram.febys.network.response.User
import com.hexagram.febys.notification.FirebaseUtils
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.ui.screens.payment.models.Wallet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IAuthRepo {
    fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseSignup>>

    fun verifyUser(
        otp: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseOtpVerification>>


    fun login(
        email: String, password: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseLogin>>

    fun resetCredentials(
        email: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Unit>>

    suspend fun fetchUserProfile(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<Profile?>>

    fun socialLogin(
        token: String, socialLogin: SocialLogin, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseLogin>>

    fun signOut()

    fun getUser(): User?

    fun getConsumer(): Consumer?

    fun getWallet(): Wallet?

    fun getSubscription(): Subscription?

    fun updateNotificationSetting(notify: Boolean)

    fun getNotificationSetting(): Boolean


    companion object {
        fun signOut(pref: IPrefManger, cartRepo: ICartRepo) {
            unsubscribeNotifications(pref)
            cartRepo.clearCart()
            pref.clear()
        }

        fun signOut(pref: IPrefManger, cartDataSource: ICartDataSource) {
            unsubscribeNotifications(pref)
            cartDataSource.clear()
            pref.clear()
        }

        private fun unsubscribeNotifications(pref: IPrefManger) {
            val topic = pref.getConsumer()?.id.toString()
            FirebaseUtils.unSubscribeToTopic(topic)
        }
    }
}