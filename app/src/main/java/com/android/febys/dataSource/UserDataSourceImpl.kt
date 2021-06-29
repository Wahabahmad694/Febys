package com.android.febys.dataSource

import com.android.febys.dto.User
import com.android.febys.prefs.IPrefManger
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val pref: IPrefManger
) : IUserDataSource {
    override fun saveUser(user: User) {
        pref.saveUser(user)
    }

    override fun getUser(): User? {
        return pref.getUser()
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

    override fun clearUserData() {
        pref.clearUser()
    }

    override fun clearUserState(){
        pref.clearAccessToken()
        pref.clearRefreshToken()
    }
}