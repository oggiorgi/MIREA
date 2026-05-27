package com.example.museflow.data.repository

import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.models.AddTrackRequest
import com.example.museflow.data.network.models.CreatePlaylistRequest
import com.example.museflow.data.network.models.PlaylistDto
import com.example.museflow.data.network.models.UpdatePlaylistRequest
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.repository.PlaylistsRepository
import kotlin.collections.map
import retrofit2.HttpException

class PlaylistsRepositoryImpl(
    private val api: ApiService
) : PlaylistsRepository {
    override suspend fun getPlaylists(): List<Playlist> {
        return api.getPlaylists().map { it.toDomain() }
    }

    override suspend fun createPlaylist(name: String, coverUrl: String?): Playlist {
        return try {
            api.createPlaylist(CreatePlaylistRequest(name, coverUrl)).toDomain()
        } catch (e: HttpException) {
            if (e.code() == 409) {
                throw Exception("Плейлист с таким именем уже создан")
            } else {
                throw e
            }
        }
    }

    override suspend fun updatePlaylist(playlistId: Int, newName: String) {
        api.updatePlaylist(playlistId, UpdatePlaylistRequest(newName))
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        api.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int): Boolean {
        return try {
            val response = api.addTrackToPlaylist(playlistId, AddTrackRequest(trackId))
            response.isSuccessful
        } catch (e: HttpException) {
            if (e.code() == 409) {
                false  // Трек уже в плейлисте
            } else {
                throw e
            }
        }
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