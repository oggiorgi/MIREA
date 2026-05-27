package com.example.museflow.data.repository

import com.example.museflow.data.local.dao.TrackDao
import com.example.museflow.data.local.entities.toDomain
import com.example.museflow.data.local.entities.toEntity
import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.models.TrackDto
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.TracksRepository
import kotlin.collections.map
import java.io.IOException

class TracksRepositoryImpl(
    private val api: ApiService,
    private val trackDao: TrackDao
) : TracksRepository {

    override suspend fun getTracks(): List<Track> {
        return try {
            // Пытаемся получить с сервера
            val tracksFromApi = api.getTracks().map { it.toDomain() }
            // Сохраняем в кэш
            trackDao.insertAll(tracksFromApi.map { it.toEntity() })
            tracksFromApi
        } catch (e: IOException) {
            // Нет интернета - берём из кэша
            val cachedTracks = trackDao.getAllTracks()
            if (cachedTracks.isNotEmpty()) {
                cachedTracks.map { it.toDomain() }
            } else {
                throw Exception("Нет подключения к интернету и нет кэшированных данных")
            }
        }
    }

    override suspend fun searchTracks(query: String): List<Track> {
        return try {
            // Пытаемся искать на сервере
            api.searchTracks(query).map { it.toDomain() }
        } catch (e: IOException) {
            // Нет интернета - ищем в кэше
            trackDao.searchTracks(query).map { it.toDomain() }
        }
    }

    override suspend fun clearCache() {
        trackDao.deleteAll()
    }
}

// Extension function для маппинга DTO в Domain модель
fun TrackDto.toDomain(): Track = Track(
    id = id,
    title = title,
    artist = artist,
    duration = duration,
    coverUrl = coverUrl,
    audioUrl = audioUrl,
    genre = genre
)