package com.example.photocatalog.data.repository

import com.example.photocatalog.data.dto.NobelPrizeResponseDto
import com.example.photocatalog.data.model.LaureateEntity
import com.example.photocatalog.data.network.KtorClient
import com.example.photocatalog.domain.model.Laureate
import com.example.photocatalog.domain.repository.NobelPrizeRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class NobelPrizeRepositoryImpl : NobelPrizeRepository {
    override suspend fun getLaureates(): List<Laureate> {
        val response = KtorClient.client.get("https://api.nobelprize.org/2.1/nobelPrizes") {
            parameter("limit", 100)
            parameter("offset", 0)
        }

        val dto = response.body<NobelPrizeResponseDto>()

        return dto.nobelPrizes.flatMap { prize ->
            prize.laureates?.mapNotNull { laureate ->  // mapNotNull пропускает null значения
                // Получаем имя из fullName или knownName
                val name = laureate.fullName?.en
                    ?: laureate.knownName?.en
                    ?: "Unknown Laureate"

                // Получаем мотивацию
                val motivation = laureate.motivation?.en ?: "No motivation provided"

                // Получаем категорию в нижнем регистре
                val category = prize.category.en.lowercase()

                LaureateEntity(
                    id = laureate.id ?: "unknown_${System.currentTimeMillis()}",
                    name = name,
                    year = prize.awardYear,
                    category = category,
                    motivation = motivation,
                    country = "Unknown",
                    portraitUrl = null,
                    portion = laureate.portion
                 )
            } ?: emptyList()
        }.map { entity ->
            Laureate(
                id = entity.id,
                name = entity.name,
                year = entity.year,
                category = entity.category,
                motivation = entity.motivation,
                country = entity.country,
                portraitUrl = entity.portraitUrl,
                portion = entity.portion
            )
        }
    }
}