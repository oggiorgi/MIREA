package com.example.photocatalog.domain.usecases

import com.example.photocatalog.domain.repository.UserRepository
import android.util.Log

class LogoutUseCase(private val repository: UserRepository) {
    suspend operator fun invoke() {
        Log.d("LogoutUseCase", "🔴 ВЫХОД: очищаем токен")
        repository.clearToken()
        Log.d("LogoutUseCase", "✅ Токен очищен. Новый токен: ${repository.getToken()}")
    }
}