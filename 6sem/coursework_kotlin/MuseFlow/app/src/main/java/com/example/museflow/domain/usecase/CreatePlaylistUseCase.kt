package com.example.museflow.domain.usecase

import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.repository.PlaylistsRepository

class CreatePlaylistUseCase(private val repository: PlaylistsRepository) {
    suspend operator fun invoke(name: String, coverUrl: String? = null): Playlist =
        repository.createPlaylist(name, coverUrl)
}