package com.example.photocatalog.domain.usecases

import com.example.photocatalog.domain.repository.UserRepository
import android.util.Log

class LoginUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(username: String, password: String): Result<String> {
        return try {
            Log.d("LoginUseCase", "Calling repository.login")
            val result = repository.login(username, password)
            if (result.isSuccess) {
                val token = result.getOrNull()
                if (token != null) {
                    Log.d("LoginUseCase", "Saving token")
                    repository.saveToken(token)
                }
            }
            result
        } catch (e: Exception) {
            Log.e("LoginUseCase", "Error in login use case", e)
            Result.failure(e)
        }
    }
}