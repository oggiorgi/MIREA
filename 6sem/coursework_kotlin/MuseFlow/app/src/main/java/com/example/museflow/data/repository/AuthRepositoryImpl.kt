package com.example.museflow.data.repository

import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.data.network.models.LoginRequest
import com.example.museflow.data.network.models.RegisterRequest
import com.example.museflow.domain.repository.AuthRepository
import java.io.IOException

class AuthRepositoryImpl(
    private val api: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(login: String, password: String): String {
        val response = api.login(LoginRequest(login, password))
        val token = response.token ?: throw IOException("Token not received")
        tokenManager.saveToken(token)
        return token
    }

    override suspend fun register(login: String, email: String, password: String): String {
        val registerResponse = api.register(RegisterRequest(login, email, password))

        if (registerResponse.message.isNullOrEmpty()) {
            throw IOException("Registration failed")
        }

        val loginResponse = api.login(LoginRequest(login, password))
        val token = loginResponse.token ?: throw IOException("Token not received after login")
        tokenManager.saveToken(token)
        return token
    }
}