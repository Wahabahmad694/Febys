package com.android.febys.di

import com.android.febys.BuildConfig
import com.android.febys.network.AuthService
import com.android.febys.network.FebysBackendService
import com.android.febys.network.FebysWebCustomizationService
import com.android.febys.network.adapter.ApiResponseCallAdapterFactory
import com.android.febys.prefs.IPrefManger
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
    fun provideOkHttpClient(pref: IPrefManger): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

        return OkHttpClient.Builder()
            .addInterceptor(
                fun(chain: Interceptor.Chain): Response {
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${pref.getAuthToken()}")
                        .build()
                    return chain.proceed(newRequest)
                }
            ).addInterceptor(HttpLoggingInterceptor().apply {
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
            .baseUrl(BuildConfig.backendBaseUrl)
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
            .baseUrl(BuildConfig.webCustomizationBaseUrl)
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
