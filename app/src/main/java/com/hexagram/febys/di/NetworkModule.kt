package com.hexagram.febys.di

import com.hexagram.febys.BuildConfig
import com.hexagram.febys.network.AuthService
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.FebysWebCustomizationService
import com.hexagram.febys.network.adapter.ApiResponseCallAdapterFactory
import com.hexagram.febys.prefs.IPrefManger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()
    }

    @Provides
    @Singleton
    @BackendClient
    fun provideBackendClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.hexagram.febys.BuildConfig.backendBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    @WebCustomizationClient
    fun provideWebCustomizationClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(com.hexagram.febys.BuildConfig.webCustomizationBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendService(@BackendClient retrofit: Retrofit): FebysBackendService {
        return retrofit.create(FebysBackendService::class.java)
    }

    @Provides
    @Singleton
    fun provideWebCustomizationService(@WebCustomizationClient retrofit: Retrofit): FebysWebCustomizationService {
        return retrofit.create(FebysWebCustomizationService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(@BackendClient retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
}
