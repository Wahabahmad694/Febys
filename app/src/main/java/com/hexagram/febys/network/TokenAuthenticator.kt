package com.hexagram.febys.network

import com.hexagram.febys.BuildConfig
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.dataSource.IUserDataSource
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.prefs.IPrefManger
import com.hexagram.febys.repos.IAuthRepo
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val pref: IPrefManger,
    private val cartDataSource: ICartDataSource,
    private val tokenRefreshService: TokenRefreshService
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = pref.getRefreshToken()
            if (refreshToken.isEmpty()) return@runBlocking null

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
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", newAccessToken).build()
            } else if (tokenResponse is ApiResponse.ApiFailureResponse.Error) {
                IAuthRepo.signOut(pref, cartDataSource)
            }
            null
        }
    }
}