package com.example.photocatalog.domain.usecase

import com.example.photocatalog.data.dto.FavoritePrizeDto
import com.example.photocatalog.domain.repository.NobelPrizeRepository

class GetFavoritesUseCase(private val repository: NobelPrizeRepository) {
    suspend operator fun invoke(): List<FavoritePrizeDto> = repository.getFavoritePrizes()
}