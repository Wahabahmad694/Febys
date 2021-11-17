package com.hexagram.febys.prefs

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.network.response.User

interface IPrefManger {
    fun saveAccessToken(accessToken: String)

    fun getAccessToken(defValue: String = ""): String

    fun saveRefreshToken(refreshToken: String)

    fun getRefreshToken(defValue: String = ""): String

    fun saveUser(user: User)

    fun saveConsumer(consumer: Consumer)

    fun getUser(): User?

    fun getConsumer(): Consumer?

    fun clearUser()

    fun clearConsumer()

    fun clearAccessToken()

    fun clearRefreshToken()

    fun toggleFav(skuId: String): Boolean

    fun addToFav(skuId: String): Boolean

    fun removeFromFav(skuId: String): Boolean

    fun removeShippingAddress():Boolean

    fun getFav(): MutableSet<String>

    fun saveFav(set: MutableSet<String>)

    fun clearFav()

    fun getDefaultShippingAddress(): ShippingAddress?

    fun saveDefaultShippingAddress(shippingAddress: ShippingAddress)

    fun getDefaultPaymentMethod(): PaymentMethod?

    fun saveDefaultPaymentMethod(paymentMethod: PaymentMethod)
}