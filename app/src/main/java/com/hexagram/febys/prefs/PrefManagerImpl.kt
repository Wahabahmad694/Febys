package com.hexagram.febys.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
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
        removeString(KEY_USER)
    }

    override fun clearAccessToken() {
        removeString(KEY_ACCESS_TOKEN)
    }

    override fun clearRefreshToken() {
        removeString(KEY_REFRESH_TOKEN)
    }

    override fun toggleFav(variantId: Int): Boolean {
        var favSetAsString = getString(KEY_FAV, "")
        val set = Utils.jsonToSetOfInt(favSetAsString)

        val addToFav = variantId !in set

        if (addToFav) {
            set.add(variantId)
        } else {
            set.remove(variantId)
        }

        favSetAsString = Utils.jsonFromSetOfInt(set)
        saveString(KEY_FAV, favSetAsString)

        return addToFav
    }

    override fun getFav(): MutableSet<Int> {
        val favSetAsString = getString(KEY_FAV, "")
        return Utils.jsonToSetOfInt(favSetAsString)
    }

    override fun saveFav(set: MutableSet<Int>) {
        val favSetAsString = Utils.jsonFromSetOfInt(set)
        saveString(KEY_FAV, favSetAsString)
    }

    override fun clearFav() {
        removeString(KEY_FAV)
    }

    private fun saveString(key: String, value: String) {
        pref.edit {
            putString(key, value)
        }
    }

    private fun getString(key: String, defValue: String): String {
        return pref.getString(key, defValue)!!
    }

    private fun removeString(key: String) {
        pref.edit {
            remove(key)
        }
    }
}