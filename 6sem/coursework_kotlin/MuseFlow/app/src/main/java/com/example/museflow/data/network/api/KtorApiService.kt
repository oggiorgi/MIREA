package com.example.museflow.data.network.api

import com.example.museflow.data.network.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.*
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

/**
 * Реализация интерфейса [ApiService] с использованием Ktor HttpClient.
 *
 * Стратегия реализации:
 * - Основные запросы (GET/POST) напрямую возвращают десериализованные DTO.
 * - Методы изменения данных (PUT/DELETE) оборачиваются в [Response] для сохранения
 * совместимости с существующей логикой обработки HTTP-статусов в слое Repository.
 */
class KtorApiService(private val client: HttpClient) : ApiService {
    override suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("login") {
            setBody(request)
        }.body()
    }

    override suspend fun register(request: RegisterRequest): AuthResponse {
        return client.post("register") {
            setBody(request)
        }.body()
    }

    override suspend fun getTracks(): List<TrackDto> {
        return client.get("tracks").body()
    }

    override suspend fun searchTracks(query: String): List<TrackDto> {
        return client.get("tracks/search") {
            parameter("q", query)
        }.body()
    }

    override suspend fun getPlaylists(): List<PlaylistDto> {
        return client.get("playlists").body()
    }

    override suspend fun createPlaylist(request: CreatePlaylistRequest): PlaylistDto {
        return client.post("playlists") {
            setBody(request)
        }.body()
    }

    override suspend fun updatePlaylist(id: Int, request: UpdatePlaylistRequest): Response<Unit> {
        return try {
            client.put("playlists/$id") {
                setBody(request)
            }
            Response.success(Unit)
        } catch (e: ResponseException) {
            Response.error(e.response.status.value, "".toResponseBody(null))
        }
    }

    override suspend fun deletePlaylist(id: Int): Response<Unit> {
        return try {
            client.delete("playlists/$id")
            Response.success(Unit)
        } catch (e: ResponseException) {
            Response.error(e.response.status.value, "".toResponseBody(null))
        }
    }

    override suspend fun addTrackToPlaylist(playlistId: Int, request: AddTrackRequest): Response<Unit> {
        return try {
            client.post("playlists/$playlistId/tracks") {
                setBody(request)
            }
            Response.success(Unit)
        } catch (e: ResponseException) {
            Response.error(e.response.status.value, "".toResponseBody(null))
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int): Response<Unit> {
        return try {
            client.delete("playlists/$playlistId/tracks/$trackId")
            Response.success(Unit)
        } catch (e: ResponseException) {
            Response.error(e.response.status.value, "".toResponseBody(null))
        }
    }
}
