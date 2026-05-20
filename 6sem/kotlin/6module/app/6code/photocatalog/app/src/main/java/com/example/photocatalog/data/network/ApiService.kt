package com.example.photocatalog.data.network

import com.example.photocatalog.data.dto.*
import com.example.photocatalog.data.local.TokenRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first

class ApiService(
    private val client: HttpClient,
    private val tokenRepository: TokenRepository
) {

    suspend fun login(login: String, password: String): LoginResponseDto =
        client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(login, password))
        }.body()

    suspend fun register(login: String, email: String, password: String): RegisterResponseDto =
        client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequestDto(login, email, password))
        }.body()

    suspend fun getAllPrizes(): List<PrizeSummaryDto> =
        client.get("/prizes") {
            addAuthHeader()  // ← добавляем токен
        }.body()

    suspend fun getPrizeDetails(year: Int, category: String): PrizeDetailDto =
        client.get("/prizes/$year/$category") {
            addAuthHeader()
        }.body()

    suspend fun getFavoritePrizes(): List<FavoritePrizeDto> =
        client.get("/users/me/prizes") {
            addAuthHeader()
        }.body()

    suspend fun addFavorite(prizeId: String): Map<String, String> =
        client.post("/users/me/prizes/$prizeId") {
            addAuthHeader()
        }.body()

    suspend fun removeFavorite(prizeId: String): Map<String, String> =
        client.delete("/users/me/prizes/$prizeId") {
            addAuthHeader()
        }.body()

    // Вспомогательная функция для добавления Bearer токена
    private suspend fun HttpRequestBuilder.addAuthHeader() {
        val token = tokenRepository.getTokenFlow().first()
        if (!token.isNullOrEmpty()) {
            header("Authorization", "Bearer $token")
        }
    }
}