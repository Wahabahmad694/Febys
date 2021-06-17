package com.android.febys.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefManagerImpl @Inject constructor(@ApplicationContext context: Context) : IPrefManger {
    companion object {
        private const val AUTH_TOKEN = "authToken"
    }

    private val pref: SharedPreferences by lazy {
        context.getSharedPreferences("febysPrefs", Context.MODE_PRIVATE)
    }

    override fun saveAuthToken(authToken: String) {
        saveString(AUTH_TOKEN, "Bearer $authToken")
    }

    override fun getAuthToken(defValue: String): String {
        return getString(AUTH_TOKEN, defValue)
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