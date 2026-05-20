package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.repository.NobelPrizeRepository

class LoginUseCase(private val repository: NobelPrizeRepository) {
    suspend operator fun invoke(login: String, password: String): String =
        repository.login(login, password)
}