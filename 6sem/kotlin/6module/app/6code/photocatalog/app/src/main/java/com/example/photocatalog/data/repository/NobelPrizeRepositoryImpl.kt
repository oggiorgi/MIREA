package com.example.photocatalog.data.repository

import com.example.photocatalog.data.dto.*
import com.example.photocatalog.data.network.ApiService
import com.example.photocatalog.domain.model.Laureate
import com.example.photocatalog.domain.repository.NobelPrizeRepository

class NobelPrizeRepositoryImpl(
    private val api: ApiService
) : NobelPrizeRepository {

    override suspend fun getLaureates(): List<Laureate> {
        // Получаем все премии с сервера
        val prizes = api.getAllPrizes()
        val result = mutableListOf<Laureate>()
        for (prize in prizes) {
            val details = api.getPrizeDetails(prize.year, prize.category)
            for (laureateResp in details.laureates) {
                result.add(
                    Laureate(
                        id = laureateResp.id,
                        name = laureateResp.name,
                        year = prize.year.toString(),
                        category = prize.category,
                        motivation = laureateResp.motivation,
                        country = "Unknown", // сервер может потом отдавать страну
                        portraitUrl = null,
                        portion = laureateResp.share.toString()
                    )
                )
            }
        }
        return result
    }

    override suspend fun login(login: String, password: String): String {
        return api.login(login, password).token
    }

    override suspend fun register(login: String, email: String, password: String): String {
        return api.register(login, email, password).token
    }

    override suspend fun getFavoritePrizes(): List<FavoritePrizeDto> {
        return api.getFavoritePrizes()
    }

    override suspend fun addFavorite(prizeId: String) {
        api.addFavorite(prizeId)
    }

    override suspend fun removeFavorite(prizeId: String) {
        api.removeFavorite(prizeId)
    }
}