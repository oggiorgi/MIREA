package com.example.photocatalog.domain.usecases

import com.example.photocatalog.domain.models.User
import com.example.photocatalog.domain.repository.UserRepository

class GetUsersUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): Result<List<User>> {
        return try {
            val token = repository.getToken()
            android.util.Log.d(
                "GetUsersUseCase",
                "🟡 Токен при загрузке: ${if (token != null) "ЕСТЬ (${token.take(20)}...)" else "null"}"
            )
            if (token.isNullOrEmpty()) {
                android.util.Log.e("GetUsersUseCase", "🔴 Токен ОТСУТСТВУЕТ!")
                Result.failure(Exception("No token found"))
            } else {
                repository.getUsers(token)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}