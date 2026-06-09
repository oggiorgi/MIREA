package com.example.museflow.data.network.api

import com.example.museflow.data.network.models.*
import retrofit2.Response

/**
 * Интерфейс API приложения. 
 * 
 * Описывает все доступные сетевые эндпоинты для работы с музыкой, плейлистами 
 * и аутентификацией. Данный интерфейс является абстракцией над сетевым слоем,
 * что позволило бесшовно переключиться с Retrofit на Ktor.
 * 
 * Примечание: Использование [Response] из Retrofit в некоторых методах сохранено
 * для совместимости с текущей архитектурой обработки ошибок в репозиториях.
 */
interface ApiService {
    suspend fun login(request: LoginRequest): AuthResponse

    suspend fun register(request: RegisterRequest): AuthResponse

    suspend fun getTracks(): List<TrackDto>

    suspend fun searchTracks(query: String): List<TrackDto>

    suspend fun getPlaylists(): List<PlaylistDto>

    suspend fun createPlaylist(request: CreatePlaylistRequest): PlaylistDto

    suspend fun updatePlaylist(id: Int, request: UpdatePlaylistRequest): Response<Unit>

    suspend fun deletePlaylist(id: Int): Response<Unit>

    suspend fun addTrackToPlaylist(playlistId: Int, request: AddTrackRequest): Response<Unit>

    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int): Response<Unit>
}
