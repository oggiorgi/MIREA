package com.example.museflow.domain.usecase

import com.example.museflow.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(login: String, password: String): String =
        repository.login(login, password)
}