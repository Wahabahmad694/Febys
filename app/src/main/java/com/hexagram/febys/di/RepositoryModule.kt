package com.hexagram.febys.di

import com.hexagram.febys.repos.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepo(repo: AuthRepoImpl): IAuthRepo

    @Singleton
    @Binds
    abstract fun bindSearchRepo(repo: SearchRepoImpl): ISearchRepo

    @Singleton
    @Binds
    abstract fun bindHomeRepo(repo: HomeRepoImpl): IHomeRepo

    @Singleton
    @Binds
    abstract fun bindProductRepo(repo: ProductRepoImpl): IProductRepo

    @Singleton
    @Binds
    abstract fun bindProductListingRepo(repo: ProductListingImpl): IProductListingRepo
}