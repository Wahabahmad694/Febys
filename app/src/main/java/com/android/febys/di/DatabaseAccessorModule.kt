package com.android.febys.di

import com.android.febys.database.IFebysDatabase
import com.android.febys.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseAccessorModule {

    @Provides
    @Singleton
    fun provideUserDao(database: IFebysDatabase): UserDao = database.userDao()
}