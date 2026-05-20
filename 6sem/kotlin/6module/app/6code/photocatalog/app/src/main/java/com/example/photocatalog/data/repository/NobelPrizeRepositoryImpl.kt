package com.example.photocatalog.data.repository

import android.util.Log
import com.example.photocatalog.data.dto.*
import com.example.photocatalog.data.network.ApiService
import com.example.photocatalog.domain.model.Laureate
import com.example.photocatalog.domain.repository.NobelPrizeRepository

class NobelPrizeRepositoryImpl(
    private val api: ApiService
) : NobelPrizeRepository {

    override suspend fun getLaureates(): List<Laureate> {
        Log.d("Repository", "=== Getting laureates ===")
        val prizes = api.getAllPrizes()
        Log.d("Repository", "Got ${prizes.size} prizes")
        val result = mutableListOf<Laureate>()
        for (prize in prizes) {
            Log.d("Repository", "Getting details for ${prize.year} - ${prize.category}")
            val details = api.getPrizeDetails(prize.year, prize.category)
            for (laureateResp in details.laureates) {
                result.add(
                    Laureate(
                        id = laureateResp.id,
                        name = laureateResp.name,
                        year = prize.year.toString(),
                        category = prize.category,
                        motivation = laureateResp.motivation,
                        country = "Unknown",
                        portraitUrl = null,
                        portion = laureateResp.share.toString()
                    )
                )
            }
        }
        Log.d("Repository", "Total laureates: ${result.size}")
        return result
    }

    override suspend fun login(login: String, password: String): String {
        return api.login(login, password).token
    }

    override suspend fun register(login: String, email: String, password: String): String {
        return api.register(login, email, password).token
    }

}