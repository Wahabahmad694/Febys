package com.android.febys.di

import com.android.febys.network.FebysBackendService
import com.android.febys.network.FebysWebCustomizationService
import com.android.febys.repos.HomeRepoImpl
import com.android.febys.repos.IHomeRepo
import com.android.febys.repos.ISearchRepo
import com.android.febys.repos.SearchRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSearchRepo(service: FebysBackendService): ISearchRepo {
        return SearchRepoImpl(service)
    }

    @Singleton
    @Provides
    fun provideHomeRepo(service: FebysWebCustomizationService): IHomeRepo {
        return HomeRepoImpl(service)
    }
}