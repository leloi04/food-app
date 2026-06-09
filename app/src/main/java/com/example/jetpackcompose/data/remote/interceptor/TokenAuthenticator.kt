package com.example.jetpackcompose.data.remote.interceptor

import com.example.jetpackcompose.data.datastore.AuthPreference
import com.example.jetpackcompose.data.remote.api.ApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val authPreference: AuthPreference,
    private val apiServiceProvider: Provider<ApiService>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            val refreshToken = authPreference.getRefreshToken().first()
            if (refreshToken == null) {
                authPreference.clearAuth()
                return@runBlocking null
            }

            val apiService = apiServiceProvider.get()
            val tokenResponse = try {
                apiService.refreshToken("Bearer $refreshToken")
            } catch (e: Exception) {
                null
            }

            if (tokenResponse?.isSuccessful == true) {
                val newTokens = tokenResponse.body()?.data
                if (newTokens?.accessToken != null && newTokens.refreshToken != null) {
                    authPreference.saveTokens(newTokens.accessToken, newTokens.refreshToken)
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                }
            }
            
            authPreference.clearAuth()
            null
        }
    }
}
