package com.hexagram.febys.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.network.response.User
import com.hexagram.febys.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : IPrefManger {
    companion object {
        private const val KEY_USER = "user"
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_FAV = "fav"
        private const val KEY_DEF_SHIPPING_ADDRESS = "defShippingAddress"
        private const val KEY_DEF_PAYMENT_METHOD = "defPaymentMethod"
    }

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("febysPrefs", Context.MODE_PRIVATE)
    }

    override fun saveAccessToken(accessToken: String) {
        saveString(KEY_ACCESS_TOKEN, "Bearer $accessToken")
    }

    override fun getAccessToken(defValue: String): String {
        return getString(KEY_ACCESS_TOKEN, defValue)
    }

    override fun saveUser(user: User) {
        val userJson = Gson().toJson(user)
        saveString(KEY_USER, userJson)
    }

    override fun getUser(): User? {
        val userJson = getString(KEY_USER, "")
        if (userJson.isEmpty()) return null
        return Gson().fromJson(userJson, User::class.java)
    }

    override fun saveRefreshToken(refreshToken: String) {
        saveString(KEY_REFRESH_TOKEN, refreshToken)
    }

    override fun getRefreshToken(defValue: String): String {
        return getString(KEY_REFRESH_TOKEN, defValue)
    }

    override fun clearUser() {
        remove(KEY_USER)
    }

    override fun clearAccessToken() {
        remove(KEY_ACCESS_TOKEN)
    }

    override fun clearRefreshToken() {
        remove(KEY_REFRESH_TOKEN)
    }

    override fun toggleFav(skuId: String): Boolean {
        val set = getFav()
        val addToFav = skuId !in set
        if (addToFav) set.add(skuId) else set.remove(skuId)
        saveFav(set)
        return addToFav
    }

    override fun addToFav(skuId: String): Boolean {
        val set = getFav()
        val addToFav = !set.contains(skuId)
        if (addToFav) {
            set.add(skuId)
            saveFav(set)
        }
        return addToFav
    }

    override fun removeFromFav(skuId: String): Boolean {
        val set = getFav()
        val removeFromFav = set.contains(skuId)
        if (removeFromFav) {
            set.remove(skuId)
            saveFav(set)
        }
        return removeFromFav
    }

    override fun getFav(): MutableSet<String> {
        return getStringSet(KEY_FAV, emptySet()).toMutableSet()
    }

    override fun saveFav(set: MutableSet<String>) {
        saveStringSet(KEY_FAV, set)
    }

    override fun clearFav() {
        remove(KEY_FAV)
    }

    override fun removeShippingAddress(): Boolean {
        getDefaultShippingAddress() ?: return false
        remove(KEY_DEF_SHIPPING_ADDRESS)
        return true
    }

    override fun getDefaultShippingAddress(): ShippingAddress? {
        val shippingAddressAsString = getString(KEY_DEF_SHIPPING_ADDRESS, "")
        if (shippingAddressAsString.isEmpty()) return null
        return Utils.jsonToShippingAddress(shippingAddressAsString)
    }

    override fun saveDefaultShippingAddress(shippingAddress: ShippingAddress) {
        val shippingAddressAsString = Utils.jsonFromShippingAddress(shippingAddress)
        saveString(KEY_DEF_SHIPPING_ADDRESS, shippingAddressAsString)
    }

    override fun getDefaultPaymentMethod(): PaymentMethod? {
        val paymentMethodAsString = getString(KEY_DEF_PAYMENT_METHOD, "")
        if (paymentMethodAsString.isEmpty()) return null
        return Utils.jsonToPaymentMethod(paymentMethodAsString)
    }

    override fun saveDefaultPaymentMethod(paymentMethod: PaymentMethod) {
        val paymentMethodAsString = Utils.jsonFromPaymentMethod(paymentMethod)
        saveString(KEY_DEF_PAYMENT_METHOD, paymentMethodAsString)
    }

    private fun saveString(key: String, value: String) {
        pref.edit {
            putString(key, value)
        }
    }

    private fun getString(key: String, defValue: String): String {
        return pref.getString(key, defValue)!!
    }

    private fun getStringSet(key: String, defValue: Set<String>): Set<String> {
        return pref.getStringSet(key, defValue)!!
    }

    private fun saveStringSet(key: String, value: Set<String>) {
        pref.edit {
            putStringSet(key, value)
        }
    }

    private fun remove(key: String) {
        pref.edit {
            remove(key)
        }
    }
}