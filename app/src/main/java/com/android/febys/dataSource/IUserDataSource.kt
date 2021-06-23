package com.android.febys.dataSource

import com.android.febys.dto.User

interface IUserDataSource {
    fun saveUser(user: User)

    fun getUser(): User?
}