package com.example.photocatalog.domain.repository

import com.example.photocatalog.domain.model.Laureate  // ← domain model

interface NobelPrizeRepository {
    suspend fun getLaureates(): List<Laureate>
}