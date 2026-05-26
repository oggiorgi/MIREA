package com.example.museflow.domain.repository

import com.example.museflow.domain.models.Track

interface TracksRepository {
    suspend fun getTracks(): List<Track>
    suspend fun searchTracks(query: String): List<Track>
}