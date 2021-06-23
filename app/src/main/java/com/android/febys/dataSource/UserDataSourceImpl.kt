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
}