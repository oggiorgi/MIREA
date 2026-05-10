package com.example.photocatalog.domain.usecases

import com.example.photocatalog.domain.models.User
import com.example.photocatalog.domain.repository.UserRepository

class GetUserDetailUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(userId: Int): Result<User> {
        return try {
            val token = repository.getToken()
            if (token.isNullOrEmpty()) {
                Result.failure(Exception("No token found"))
            } else {
                repository.getUserDetail(token, userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}