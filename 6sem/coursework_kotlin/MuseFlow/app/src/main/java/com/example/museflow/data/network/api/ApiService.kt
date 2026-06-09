package com.example.museflow.data.network.api

import com.example.museflow.data.network.models.*
import retrofit2.Response

/*
 * Интерфейс API приложения. 
 * Описывает все доступные сетевые эндпоинты для работы с музыкой, 
 * плейлистами и аутентификацией.
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