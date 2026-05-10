package com.example.photocatalog.data.remote.api

import com.example.photocatalog.data.remote.dto.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class AuthApi(private val client: HttpClient) {
    
    suspend fun login(username: String, password: String): LoginResponseDto {
        return client.post {
            url("https://dummyjson.com/auth/login")
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username, password))
        }.body()
    }
    
    suspend fun getUsers(token: String): UsersResponseDto {
        return client.get {
            url("https://dummyjson.com/users")
            header("Authorization", "Bearer $token")
        }.body()
    }
    
    suspend fun getUserDetail(token: String, userId: Int): UserDto {
        return client.get {
            url("https://dummyjson.com/users/$userId")
            header("Authorization", "Bearer $token")
        }.body()
    }
}