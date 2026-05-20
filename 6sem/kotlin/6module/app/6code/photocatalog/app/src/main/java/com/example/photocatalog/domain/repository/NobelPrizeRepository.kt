package com.example.photocatalog.domain.repository

import com.example.photocatalog.domain.model.Laureate

interface NobelPrizeRepository {
    suspend fun getLaureates(): List<Laureate>
    suspend fun login(login: String, password: String): String
    suspend fun register(login: String, email: String, password: String): String
}