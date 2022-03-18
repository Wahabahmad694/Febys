package com.hexagram.febys.prefs

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.subscription.Subscription
import com.hexagram.febys.network.response.User
import com.hexagram.febys.ui.screens.payment.models.Wallet

interface IPrefManger {
    fun saveAccessToken(accessToken: String)

    fun getAccessToken(defValue: String = ""): String

    fun saveRefreshToken(refreshToken: String)

    fun getRefreshToken(defValue: String = ""): String

    fun saveUser(user: User)

    fun saveConsumer(consumer: Consumer)

    fun getUser(): User?

    fun getConsumer(): Consumer?

    fun toggleFav(skuId: String): Boolean

    fun addToFav(skuId: String): Boolean

    fun removeFromFav(skuId: String): Boolean

    fun removeShippingAddress(): Boolean

    fun getFav(): MutableSet<String>

    fun saveFav(set: MutableSet<String>)

    fun getDefaultShippingAddress(): ShippingAddress?

    fun saveDefaultShippingAddress(shippingAddress: ShippingAddress)

    fun saveWallet(wallet: Wallet)

    fun getWallet(): Wallet?

    fun clear()

    fun saveSubscription(subscription: Subscription)

    fun getSubscription(): Subscription?

    fun saveNotificationCount(count: Int)

    fun getNotificationCount(defValue: Int = 0): Int

    fun clearNotificationCount()
}