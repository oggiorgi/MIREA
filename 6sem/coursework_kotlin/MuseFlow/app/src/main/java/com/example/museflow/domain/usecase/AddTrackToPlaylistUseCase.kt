package com.example.museflow.domain.usecase

import com.example.museflow.domain.repository.PlaylistsRepository

class AddTrackToPlaylistUseCase(private val repository: PlaylistsRepository) {
    suspend operator fun invoke(playlistId: Int, trackId: Int) =
        repository.addTrackToPlaylist(playlistId, trackId)
}