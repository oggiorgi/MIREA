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

/*
 * Реализация репозитория треков с поддержкой офлайн-режима.
 * 
 * Стратегия: "Сначала сеть, затем кэш". 
 * Это позволяет пользователю всегда видеть актуальный контент при наличии интернета, 
 * но сохраняет работоспособность приложения (чтение ранее загруженных данных) в офлайне.
 */
class TracksRepositoryImpl(
    private val api: ApiService,
    private val trackDao: TrackDao
) : TracksRepository {

    override suspend fun getTracks(): List<Track> {
        return try {
            val tracksFromApi = api.getTracks().map { it.toDomain() }
            trackDao.insertAll(tracksFromApi.map { it.toEntity() })
            tracksFromApi
        } catch (e: IOException) {
            /* 
             * При сетевой ошибке переключаемся на локальное хранилище. 
             * Мы выбрасываем исключение только если кэш пуст, чтобы UI мог 
             * показать пользователю понятное сообщение о невозможности загрузки.
             */
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
            api.searchTracks(query).map { it.toDomain() }
        } catch (e: IOException) {
            // Поиск по локальному кэшу в случае отсутствия соединения.
            trackDao.searchTracks(query).map { it.toDomain() }
        }
    }

    override suspend fun clearCache() {
        trackDao.deleteAll()
    }
}

// Функции расширения для преобразования моделей данных (DTO) в доменные модели
fun TrackDto.toDomain(): Track = Track(
    id = id,
    title = title,
    artist = artist,
    duration = duration,
    coverUrl = coverUrl,
    audioUrl = audioUrl,
    genre = genre
)