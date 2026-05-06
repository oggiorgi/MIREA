package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.model.Laureate  // ← domain model
import com.example.photocatalog.domain.repository.NobelPrizeRepository

class GetLaureatesUseCase(
    private val repository: NobelPrizeRepository
) {
    suspend operator fun invoke(): List<Laureate> {
        return repository.getLaureates()
    }
}