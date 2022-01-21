package com.hexagram.febys.di

import com.hexagram.febys.BuildConfig
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.dataSource.IUserDataSource
import com.hexagram.febys.network.*
import com.hexagram.febys.network.adapter.ApiResponseCallAdapterFactory
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.ui.screens.payment.service.PaymentService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val interceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        pref: IPrefManger,
        userDataSource: IUserDataSource,
        cartDataSource: ICartDataSource
    ): AuthenticationInterceptor {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.backendBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()

        val service = retrofit.create(TokenRefreshService::class.java)

        return AuthenticationInterceptor(pref, userDataSource, cartDataSource, service)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authenticator: AuthenticationInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(authenticator)
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

    @Provides
    @Singleton
    fun providePaymentService(@BackendClient retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }
}
