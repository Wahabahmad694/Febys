package com.hexagram.febys.prefs

import com.hexagram.febys.models.view.ShippingAddress
import com.hexagram.febys.network.response.User

interface IPrefManger {
    fun saveAccessToken(accessToken: String)

    fun getAccessToken(defValue: String = ""): String

    fun saveRefreshToken(refreshToken: String)

    fun getRefreshToken(defValue: String = ""): String

    fun saveUser(user: User)

    fun getUser(): User?

    fun clearUser()

    fun clearAccessToken()

    fun clearRefreshToken()

    fun toggleFav(variantId: Int): Boolean

    fun addToFav(variantId: Int): Boolean

    fun removeFromFav(variantId: Int): Boolean

    fun getFav(): MutableSet<Int>

    fun saveFav(set: MutableSet<Int>)

    fun clearFav()

    fun getDefaultShippingAddress(): ShippingAddress?

    fun saveDefaultShippingAddress(shippingAddress: ShippingAddress)
}