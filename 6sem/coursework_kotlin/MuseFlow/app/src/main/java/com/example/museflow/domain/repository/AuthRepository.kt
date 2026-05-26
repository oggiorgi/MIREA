package com.example.museflow.domain.repository

interface AuthRepository {
    suspend fun login(login: String, password: String): String
    suspend fun register(login: String, email: String, password: String): String
}