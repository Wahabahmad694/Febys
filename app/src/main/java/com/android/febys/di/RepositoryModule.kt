package com.android.febys.di

import com.android.febys.database.dao.UserDao
import com.android.febys.network.AuthService
import com.android.febys.network.FebysBackendService
import com.android.febys.network.FebysWebCustomizationService
import com.android.febys.prefs.IPrefManger
import com.android.febys.repos.*
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
    fun provideAuthRepo(service: AuthService, userDao: UserDao, pref: IPrefManger): IAuthRepo {
        return AuthRepoImpl(service, userDao, pref)
    }

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

    @Singleton
    @Provides
    fun provideProductRepo(service: FebysBackendService): IProductRepo {
        return ProductRepoImpl(service)
    }
}