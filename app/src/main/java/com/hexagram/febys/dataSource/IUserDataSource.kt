package com.hexagram.febys.dataSource

import com.hexagram.febys.network.response.User

interface IUserDataSource {
    fun saveUser(user: User)

    fun getUser(): User?

    fun saveAccessToken(token: String)

    fun getAccessToken(): String

    fun saveRefreshToken(token: String)

    fun getRefreshToken(): String

    fun clearUserData()

    fun clearUserState()
}