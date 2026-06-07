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

/*
 * Репозиторий для управления плейлистами.
 * Инкапсулирует логику сетевых запросов и обработку специфических HTTP ошибок.
 */
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
            /* 
             * Обработка конфликта (409): если плейлист с таким именем уже есть, 
             * выбрасываем информативное исключение для отображения в UI.
             */
            if (e.code() == 409) {
                throw Exception("Плейлист с таким именем уже создан")
            } else {
                throw e
            }
        }
    }

    override suspend fun updatePlaylist(playlistId: Int, newName: String) {
        val response = api.updatePlaylist(playlistId, UpdatePlaylistRequest(newName))
        if (!response.isSuccessful) throw HttpException(response)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val response = api.deletePlaylist(playlistId)
        if (!response.isSuccessful) throw HttpException(response)
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int): Boolean {
        return try {
            val response = api.addTrackToPlaylist(playlistId, AddTrackRequest(trackId))
            response.isSuccessful
        } catch (e: HttpException) {
            if (e.code() == 409) {
                false
            } else {
                throw e
            }
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int) {
        val response = api.removeTrackFromPlaylist(playlistId, trackId)
        if (!response.isSuccessful) throw HttpException(response)
    }
}

// Extension function для маппинга DTO в Domain модель
fun PlaylistDto.toDomain(): Playlist = Playlist(
    id = id,
    name = name,
    coverUrl = coverUrl,
    tracks = tracks.map { it.toDomain() }
)