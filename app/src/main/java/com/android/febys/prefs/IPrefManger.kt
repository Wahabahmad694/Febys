package com.android.febys.prefs

import com.android.febys.dto.User

interface IPrefManger {
    fun getAuthToken(defValue: String = ""): String

    fun getRefreshToken(defValue: String = ""): String

    fun saveUser(user: User)

    fun getUser(): User?
}