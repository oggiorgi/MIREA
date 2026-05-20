package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.repository.NobelPrizeRepository

class RegisterUseCase(private val repository: NobelPrizeRepository) {
    suspend operator fun invoke(login: String, email: String, password: String): String =
        repository.register(login, email, password)
}