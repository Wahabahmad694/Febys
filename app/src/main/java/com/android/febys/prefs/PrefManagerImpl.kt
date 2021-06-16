package com.android.febys.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.febys.utils.PrefKeys

class PrefManagerImpl(private val pref: SharedPreferences) : IPrefManger {

    override fun saveAuthToken(authToken: String) {
        saveString(PrefKeys.AUTH_TOKEN, authToken)
    }

    override fun getAuthToken(defValue: String): String {
        return getString(PrefKeys.AUTH_TOKEN, defValue)
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