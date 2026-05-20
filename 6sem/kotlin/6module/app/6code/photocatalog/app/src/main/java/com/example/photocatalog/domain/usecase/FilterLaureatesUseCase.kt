package com.example.photocatalog.domain.usecase

import com.example.photocatalog.domain.model.Laureate

class FilterLaureatesUseCase {
    operator fun invoke(
        laureates: List<Laureate>,
        year: String?,
        category: String?
    ): List<Laureate> {
        return laureates.filter { laureate ->
            (year == null || laureate.year == year) &&
                    (category == null || laureate.category == category.lowercase())
        }
    }
}