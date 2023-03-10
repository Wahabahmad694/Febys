package com.hexagram.febys.dataSource

import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.response.User
import com.hexagram.febys.prefs.IPrefManger
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val pref: IPrefManger
) : IUserDataSource {
    override fun saveUser(user: User) {
        pref.saveUser(user)
    }

    override fun saveConsumer(consumer: Consumer) {
        pref.saveConsumer(consumer)
    }

    override fun getUser(): User? {
        return pref.getUser()
    }

    override fun getConsumer(): Consumer? {
        return pref.getConsumer()
    }

    override fun saveAccessToken(token: String) {
        pref.saveAccessToken(token)
    }

    override fun getAccessToken(): String {
        return pref.getAccessToken()
    }

    override fun saveRefreshToken(token: String) {
        pref.saveRefreshToken(token)
    }

    override fun getRefreshToken(): String {
        return pref.getRefreshToken()
    }
}