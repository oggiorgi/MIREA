package com.example.museflow.domain.usecase

import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.TracksRepository

class GetTracksUseCase(private val repository: TracksRepository) {
    suspend operator fun invoke(): List<Track> = repository.getTracks()
}