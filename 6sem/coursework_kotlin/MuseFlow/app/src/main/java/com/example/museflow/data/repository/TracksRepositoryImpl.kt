package com.example.museflow.data.repository

import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.models.TrackDto
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.TracksRepository
import kotlin.collections.map

class TracksRepositoryImpl(
    private val api: ApiService
) : TracksRepository {
    override suspend fun getTracks(): List<Track> {
        return api.getTracks().map { it.toDomain() }
    }

    override suspend fun searchTracks(query: String): List<Track> {
        return api.searchTracks(query).map { it.toDomain() }
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