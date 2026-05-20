package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.repository.NobelPrizeRepository

class AddFavoriteUseCase(private val repository: NobelPrizeRepository) {
    suspend operator fun invoke(prizeId: String) = repository.addFavorite(prizeId)
}