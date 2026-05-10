package com.example.photocatalog.data.repository

import com.example.photocatalog.data.local.TokenDataStore
import com.example.photocatalog.data.mappers.toDomain
import com.example.photocatalog.data.remote.api.AuthApi
import com.example.photocatalog.domain.models.User
import com.example.photocatalog.domain.repository.UserRepository

class UserRepositoryImpl(
    private val api: AuthApi,
    private val tokenDataStore: TokenDataStore
) : UserRepository {
    
    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response = api.login(username, password)
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUsers(token: String): Result<List<User>> {
        return try {
            val response = api.getUsers(token)
            Result.success(response.users.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserDetail(token: String, userId: Int): Result<User> {
        return try {
            val response = api.getUserDetail(token, userId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun saveToken(token: String) {
        tokenDataStore.saveToken(token)
    }
    
    override suspend fun getToken(): String? {
        return tokenDataStore.getToken()
    }
    
    override suspend fun clearToken() {
        tokenDataStore.clearToken()
    }
}