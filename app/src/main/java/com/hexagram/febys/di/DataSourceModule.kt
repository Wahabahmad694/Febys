package com.hexagram.febys.di

import com.hexagram.febys.dataSource.IUserDataSource
import com.hexagram.febys.dataSource.UserDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun provideUserDataSource(userDataSource: UserDataSourceImpl): IUserDataSource
}