package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.repository.NobelPrizeRepository

class RemoveFavoriteUseCase(private val repository: NobelPrizeRepository) {
    suspend operator fun invoke(prizeId: String) = repository.removeFavorite(prizeId)
}