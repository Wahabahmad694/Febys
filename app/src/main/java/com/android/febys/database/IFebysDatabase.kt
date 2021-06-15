package com.android.febys.database

import com.android.febys.database.dao.UserDao

interface IFebysDatabase {
    fun userDao(): UserDao
}