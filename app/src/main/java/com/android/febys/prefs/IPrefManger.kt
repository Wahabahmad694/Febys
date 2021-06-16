package com.android.febys.prefs

interface IPrefManger {
    fun saveAuthToken(authToken: String)

    fun getAuthToken(defValue: String = ""): String
}