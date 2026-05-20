package com.example.photocatalog.data.network

import android.util.Log
import com.example.photocatalog.data.dto.*
import com.example.photocatalog.data.local.TokenRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first

class ApiService(
    private val client: HttpClient,
    private val tokenRepository: TokenRepository
) {

    private suspend fun <T> handleAuthenticatedRequest(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Log.w("ApiService", "🔄 401 Unauthorized! Clearing token...")
                tokenRepository.clearToken()
                throw Exception("Session expired. Please login again.")
            } else {
                throw e
            }
        }
    }

    suspend fun login(login: String, password: String): LoginResponseDto {
        Log.d("ApiService", "=== POST /login ===")
        return try {
            val response = client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDto(login, password))
            }
            Log.d("ApiService", "Login success, status: ${response.status}")
            response.body()
        } catch (e: Exception) {
            Log.e("ApiService", "Login error", e)
            throw e
        }
    }

    suspend fun register(login: String, email: String, password: String): RegisterResponseDto =
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequestDto(login, email, password))
        }.body()

    suspend fun getAllPrizes(): List<PrizeSummaryDto> = handleAuthenticatedRequest {
        Log.d("ApiService", "=== GET /prizes ===")
        val response: HttpResponse = client.get("/prizes") {
            addAuthHeader()
        }
        Log.d("ApiService", "Response status: ${response.status}")
        response.body()
    }

    suspend fun getPrizeDetails(year: Int, category: String): PrizeDetailDto = handleAuthenticatedRequest {
        Log.d("ApiService", "=== GET /prizes/$year/$category ===")
        client.get("/prizes/$year/$category") {
            addAuthHeader()
        }.body()
    }

    private suspend fun HttpRequestBuilder.addAuthHeader() {
        val token = tokenRepository.getTokenFlow().first()
        Log.d("ApiService", "Token: ${token?.take(50)}")
        if (!token.isNullOrEmpty()) {
            headers.remove("Authorization")
            header("Authorization", "Bearer $token")
            Log.d("ApiService", "✅ Authorization header added")
        } else {
            Log.w("ApiService", "⚠️ No token available!")
        }
    }
}