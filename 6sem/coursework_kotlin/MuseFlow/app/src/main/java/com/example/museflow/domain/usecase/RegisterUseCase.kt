package com.example.museflow.domain.usecase

import com.example.museflow.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(login: String, email: String, password: String): String =
        repository.register(login, email, password)
}