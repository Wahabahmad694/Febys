package com.android.febys.prefs

import com.android.febys.dto.User

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
}