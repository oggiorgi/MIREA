package com.example.museflow.domain.usecase

import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.repository.PlaylistsRepository

class GetPlaylistsUseCase(private val repository: PlaylistsRepository) {
    suspend operator fun invoke(): List<Playlist> = repository.getPlaylists()
}