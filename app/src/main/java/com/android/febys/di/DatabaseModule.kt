package com.android.febys.di

import androidx.room.Room
import com.android.febys.FebysApp
import com.android.febys.database.FebysDatabase
import com.android.febys.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: FebysApp): FebysDatabase =
        Room.databaseBuilder(context, FebysDatabase::class.java, "FebysDB")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserDao(database: FebysDatabase): UserDao = database.userDao()

}