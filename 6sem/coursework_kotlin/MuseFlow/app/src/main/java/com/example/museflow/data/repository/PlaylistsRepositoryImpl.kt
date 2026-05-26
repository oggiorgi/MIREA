package com.example.museflow.data.repository

import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.models.AddTrackRequest
import com.example.museflow.data.network.models.CreatePlaylistRequest
import com.example.museflow.data.network.models.PlaylistDto
import com.example.museflow.data.network.models.UpdatePlaylistRequest
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.repository.PlaylistsRepository
import kotlin.collections.map

class PlaylistsRepositoryImpl(
    private val api: ApiService
) : PlaylistsRepository {
    override suspend fun getPlaylists(): List<Playlist> {
        return api.getPlaylists().map { it.toDomain() }
    }

    override suspend fun createPlaylist(name: String, coverUrl: String?): Playlist {
        return api.createPlaylist(CreatePlaylistRequest(name, coverUrl)).toDomain()
    }

    override suspend fun updatePlaylist(playlistId: Int, newName: String) {
        api.updatePlaylist(playlistId, UpdatePlaylistRequest(newName))
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        api.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int) {
        api.addTrackToPlaylist(playlistId, AddTrackRequest(trackId))
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        api.removeTrackFromPlaylist(playlistId, trackId)
    }
}

// Extension function для маппинга DTO в Domain модель
fun PlaylistDto.toDomain(): Playlist = Playlist(
    id = id,
    name = name,
    coverUrl = coverUrl,
    tracks = tracks.map { it.toDomain() }
)