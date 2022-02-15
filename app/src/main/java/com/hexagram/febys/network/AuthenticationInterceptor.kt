package com.hexagram.febys.network

import com.hexagram.febys.BuildConfig
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.repos.IAuthRepo
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor(
    private val pref: IPrefManger,
    private val cartDataSource: ICartDataSource,
    private val tokenRefreshService: TokenRefreshService
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            val originalRequest: Request = chain.request()
            val origResponse: Response = chain.proceed(originalRequest)

            if (origResponse.code in arrayOf(401, 403)) {
                val refreshToken = pref.getRefreshToken()
                if (refreshToken.isEmpty()) return@runBlocking origResponse

                val fields = mapOf(
                    "grant_type" to "refresh_token",
                    "client_id" to BuildConfig.keycloakClientId,
                    "refresh_token" to refreshToken,
                    "client_secret" to BuildConfig.keycloakClientSecret
                )
                val baseUrl = BuildConfig.backendBaseUrl.replace("/api", "/auth")
                val url = "${baseUrl}realms/febys-consumers/protocol/openid-connect/token"
                val tokenResponse = tokenRefreshService.refreshToken(url, fields)
                if (tokenResponse is ApiResponse.ApiSuccessResponse) {
                    val newAccessToken = tokenResponse.data!!.accessToken
                    val newRefreshToken = tokenResponse.data.refreshToken
                    pref.saveAccessToken(newAccessToken)
                    pref.saveRefreshToken(newRefreshToken)
                    pref.getAccessToken()
                    val newReq = originalRequest.newBuilder()
                        .header("Authorization", pref.getAccessToken()).build()
                    origResponse.close()
                    return@runBlocking chain.proceed(newReq)
                } else if (tokenResponse is ApiResponse.ApiFailureResponse.Error) {
                    IAuthRepo.signOut(pref, cartDataSource)
                }
            }
            origResponse
        }
    }
}