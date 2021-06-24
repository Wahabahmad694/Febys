package com.android.febys.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.febys.dto.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefManagerImpl @Inject constructor(@ApplicationContext context: Context) : IPrefManger {
    companion object {
        private const val KEY_USER = "user"
    }

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("febysPrefs", Context.MODE_PRIVATE)
    }

    override fun getAuthToken(defValue: String): String {
        return getUser()?.accessToken?.let { "Bearer $it" } ?: defValue
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

    override fun getRefreshToken(defValue: String): String {
        return getUser()?.refreshToken ?: defValue
    }

    private fun saveString(key: String, value: String) {
        pref.edit {
            putString(key, value)
        }
    }

    private fun getString(key: String, defValue: String): String {
        return pref.getString(key, defValue)!!
    }
}