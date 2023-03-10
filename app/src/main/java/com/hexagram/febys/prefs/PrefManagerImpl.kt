package com.hexagram.febys.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.api.subscription.Subscription
import com.hexagram.febys.network.response.User
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.utils.Utils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : IPrefManger {
    companion object {
        private const val KEY_USER = "user"
        private const val KEY_CONSUMER = "consumer"
        private const val KEY_SUBSCRIPTION = "subscription"
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
        private const val KEY_FAV = "fav"
        private const val KEY_DEF_SHIPPING_ADDRESS = "defShippingAddress"
        private const val KEY_WALLET = "wallet"
        private const val KEY_NOTIFICATION_COUNT = "notificationCount"
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

    override fun saveConsumer(consumer: Consumer) {
        val userJson = Gson().toJson(consumer)
        saveString(KEY_CONSUMER, userJson)
    }

    override fun getUser(): User? {
        val userJson = getString(KEY_USER, "")
        if (userJson.isEmpty()) return null
        return Gson().fromJson(userJson, User::class.java)
    }

    override fun getConsumer(): Consumer? {
        val consumerJson = getString(KEY_CONSUMER, "")
        if (consumerJson.isEmpty()) return null
        return Gson().fromJson(consumerJson, Consumer::class.java)
    }

    override fun saveRefreshToken(refreshToken: String) {
        saveString(KEY_REFRESH_TOKEN, refreshToken)
    }

    override fun getRefreshToken(defValue: String): String {
        return getString(KEY_REFRESH_TOKEN, defValue)
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

    override fun saveSubscription(subscription: Subscription) {
        val subscriptionJson = Gson().toJson(subscription)
        saveString(KEY_SUBSCRIPTION, subscriptionJson)
    }

    override fun getSubscription(): Subscription? {
        val subscriptionJson = getString(KEY_SUBSCRIPTION, "")
        if (subscriptionJson.isEmpty()) return null
        return Gson().fromJson(subscriptionJson, Subscription::class.java)
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

    override fun saveWallet(wallet: Wallet) {
        val walletAsString = Utils.jsonFromWallet(wallet)
        saveString(KEY_WALLET, walletAsString)
    }

    override fun getWallet(): Wallet? {
        val wallet = getString(KEY_WALLET, "")
        if (wallet.isEmpty()) return null
        return Utils.jsonToWallet(wallet)
    }

    override fun saveNotificationCount(count: Int) {
        saveInt(KEY_NOTIFICATION_COUNT, count)
    }

    override fun getNotificationCount(defValue: Int): Int {
        return getInt(KEY_NOTIFICATION_COUNT, defValue)
    }

    override fun clearNotificationCount() {
        remove(KEY_NOTIFICATION_COUNT)
    }

    private fun saveBoolean(key: String, value: Boolean) {
        pref.edit {
            putBoolean(key, value)
        }
    }

    private fun getBoolean(key: String, defValue: Boolean): Boolean {
        return pref.getBoolean(key, defValue)
    }

    private fun saveInt(key: String, value: Int) {
        pref.edit {
            putInt(key, value)
        }
    }

    private fun getInt(key: String, defValue: Int): Int {
        return pref.getInt(key, defValue)
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

    override fun clear() = pref.edit { clear() }
}