package com.example.photocatalog.domain.repository

import com.example.photocatalog.domain.models.User

interface UserRepository {
    suspend fun login(username: String, password: String): Result<String> // возвращает token
    suspend fun getUsers(token: String): Result<List<User>>
    suspend fun getUserDetail(token: String, userId: Int): Result<User>
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}