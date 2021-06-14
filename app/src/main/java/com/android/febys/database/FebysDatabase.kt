package com.android.febys.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.febys.database.dao.UserDao
import com.android.febys.dto.UserDTO

@Database(entities = [UserDTO::class], version = 1)
abstract class FebysDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}