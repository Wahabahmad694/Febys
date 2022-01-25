package com.hexagram.febys.dataSource

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.response.User

interface IUserDataSource {
    fun saveUser(user: User)

    fun saveConsumer(consumer: Consumer)

    fun getUser(): User?

    fun getConsumer(): Consumer?

    fun saveAccessToken(token: String)

    fun getAccessToken(): String

    fun saveRefreshToken(token: String)

    fun getRefreshToken(): String
}