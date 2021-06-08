package com.android.febys.di

import com.android.febys.BuildConfig
import com.android.febys.network.FebysBackendService
import com.android.febys.network.FebysWebCustomizationService
import com.android.febys.network.adapter.ApiResponseCallAdapterFactory
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
        val token =
            "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJWYkhlYmI1TjJrVVdvOXdwdmN4YXQ5cXdUWXhDQV9BNjNZd2l3N2U4clhvIn0.eyJleHAiOjE2MjI2NTE4NTEsImlhdCI6MTYyMjYxNTk5MywiYXV0aF90aW1lIjoxNjIyNjE1ODUxLCJqdGkiOiJjOTRhNDE1YS03YjY1LTQyZWEtYjJmYS04NzcyMzQzZDJkMTEiLCJpc3MiOiJodHRwOi8va2V5Y2xvYWsuc2VydmVyOjU0MzMvYXV0aC9yZWFsbXMvZmVieXMtYWRtaW5zIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjdkMzYxMmVkLWEwYjItNDFlNS04Njg0LWJmZmIyOWE0YmQxYiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZlYnlzLWFkbWluLXBhbmVsIiwibm9uY2UiOiIzOWRmMTdiNy05ODhkLTQzN2MtYjY3OS1lMDQzOWQwOGYxZjkiLCJzZXNzaW9uX3N0YXRlIjoiZDhiZjFiZjAtZDg1ZC00OWE4LTk0MjgtNDA2NTFiMmVmOTJjIiwiYWNyIjoiMCIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vcWEuZmVieXMuY29tOjUwMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiU3VwZXIgQWRtaW4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbiIsImdpdmVuX25hbWUiOiJTdXBlciIsImZhbWlseV9uYW1lIjoiQWRtaW4iLCJlbWFpbCI6ImFkbWluQGZlYnlzLmNvbSJ9.Tx-UXmDzZuz5Y1cnwtk15inMo6Sq9Rm9HURUUy9SdUJU-jQHNzRp_NcGnRMgHCTL9JnoJV-QUkvvF6EdsRsVU_Ijopbr5zqwG7XfGM3ghQgQL1hG13Szz2sAVbpYvVeUFTEBAXV6bBmkvPq6TAyDxJkOu6Y1w964tIdfjnkxrIp1wYCrtq2H65SStzYjCJnh_JMEOYcHqTV0GYsBrFzWTsv7ijzqq7TXrcy-4JwJ-Cf43EbjgGE4zLsfclBSXyNjkissqHm2sa9b5O0L3WwI_nf7HYuEOYoWb_2Fs-T-Kqw8OTSzEYAVu4whGdV15mLFatjpb6zFyZwtLidXpQ7_HQ"

        val interceptor = HttpLoggingInterceptor()
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }

        return OkHttpClient.Builder()
            .addInterceptor(
                fun(chain: Interceptor.Chain): Response {
                    val newRequest = chain.request().newBuilder()
                        .addHeader(
                            "Authorization",
                            "Bearer $token"   // will be update from pref when auth(sign-in/sign-up) part implement
                        )
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
    @FebysBackendClient
    fun provideRetrofitBackend(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.backendBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    @FebysWebCustomizationClient
    fun provideRetrofitWebCustomization(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.webCustomizationBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideFebysBackendService(@FebysBackendClient retrofit: Retrofit): FebysBackendService {
        return retrofit.create(FebysBackendService::class.java)
    }

    @Provides
    @Singleton
    fun provideFebysWebCustomizationService(@FebysWebCustomizationClient retrofit: Retrofit): FebysWebCustomizationService {
        return retrofit.create(FebysWebCustomizationService::class.java)
    }
}
