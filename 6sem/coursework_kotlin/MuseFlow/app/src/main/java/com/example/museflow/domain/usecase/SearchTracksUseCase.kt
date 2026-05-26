package com.example.museflow.domain.usecase

import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.TracksRepository

class SearchTracksUseCase(private val repository: TracksRepository) {
    suspend operator fun invoke(query: String): List<Track> = repository.searchTracks(query)
}