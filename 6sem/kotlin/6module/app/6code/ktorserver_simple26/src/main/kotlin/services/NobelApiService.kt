package org.example.services

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*
import org.example.database.Prizes
import org.example.database.Laureates
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

class NobelApiService {
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 60000
        }
    }

    suspend fun fetchAndStorePrizes() {
        try {
            val response = client.get("https://api.nobelprize.org/2.1/nobelPrizes")
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            parseAndSavePrizes(json)
        } catch (e: Exception) {
            println("Failed to fetch Nobel prizes: ${e.message}")
        }
    }

    private fun parseAndSavePrizes(json: JsonObject) {
        val prizesArray = json["nobelPrizes"]?.jsonArray ?: return

        for (prizeElement in prizesArray) {
            val prizeObj = prizeElement.jsonObject

            val id = prizeObj["id"]?.jsonPrimitive?.content ?: continue
            val awardYear = prizeObj["awardYear"]?.jsonPrimitive?.content?.toIntOrNull() ?: continue
            val category = prizeObj["category"]?.jsonPrimitive?.content ?: continue

            // Получаем английское название категории
            val categoryFullNameObj = prizeObj["categoryFullName"]?.jsonObject
            val categoryFullName = categoryFullNameObj?.get("en")?.jsonPrimitive?.content ?: category

            // Сохраняем премию в БД
            transaction {
                Prizes.insert {
                    it[Prizes.id] = id
                    it[Prizes.awardYear] = awardYear
                    it[Prizes.category] = category
                    it[Prizes.categoryFullName] = categoryFullName
                }
            }

            // Парсим лауреатов
            val laureatesArray = prizeObj["laureates"]?.jsonArray
            laureatesArray?.forEach { laureateElement ->
                val laureateObj = laureateElement.jsonObject
                val laureateId = laureateObj["id"]?.jsonPrimitive?.content ?: return@forEach

                val fullName = laureateObj["fullName"]?.jsonObject
                    ?.get("en")?.jsonPrimitive?.content ?: ""

                val motivation = laureateObj["motivation"]?.jsonObject
                    ?.get("en")?.jsonPrimitive?.content ?: ""

                val share = laureateObj["share"]?.jsonPrimitive?.content?.toIntOrNull() ?: 1

                transaction {
                    Laureates.insert {
                        it[Laureates.id] = laureateId
                        it[Laureates.prizeId] = id
                        it[Laureates.fullName] = fullName
                        it[Laureates.motivation] = motivation
                        it[Laureates.share] = share
                    }
                }
            }
        }
        println("Successfully fetched and stored Nobel prizes")
    }
}