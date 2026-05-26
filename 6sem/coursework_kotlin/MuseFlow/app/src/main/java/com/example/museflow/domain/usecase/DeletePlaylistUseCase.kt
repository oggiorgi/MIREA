package com.example.museflow.domain.usecase

import com.example.museflow.domain.repository.PlaylistsRepository

class DeletePlaylistUseCase(private val repository: PlaylistsRepository) {
    suspend operator fun invoke(playlistId: Int) = repository.deletePlaylist(playlistId)
}