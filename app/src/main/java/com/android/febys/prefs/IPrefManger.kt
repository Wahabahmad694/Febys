package com.android.febys.prefs

import com.android.febys.dto.User

interface IPrefManger {
    fun saveAuthToken(authToken: String)

    fun getAuthToken(defValue: String = ""): String

    fun saveUser(user: User)

    fun getUser(): User?
}