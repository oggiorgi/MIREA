package com.example.museflow.data.network.client

import android.util.Log
import com.example.museflow.data.network.auth.TokenManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun provideHttpClient(tokenManager: TokenManager): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("KtorClient", message)
                    }
                }
                level = LogLevel.BODY
            }

            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
                tokenManager.getToken()?.let { token ->
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }
}
