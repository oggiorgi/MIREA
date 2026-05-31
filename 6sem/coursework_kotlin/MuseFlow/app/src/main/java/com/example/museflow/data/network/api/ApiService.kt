package com.example.museflow.data.network.api

import com.example.museflow.data.network.models.AddTrackRequest
import com.example.museflow.data.network.models.AuthResponse
import com.example.museflow.data.network.models.CreatePlaylistRequest
import com.example.museflow.data.network.models.LoginRequest
import com.example.museflow.data.network.models.PlaylistDto
import com.example.museflow.data.network.models.RegisterRequest
import com.example.museflow.data.network.models.TrackDto
import com.example.museflow.data.network.models.UpdatePlaylistRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("/tracks")
    suspend fun getTracks(): List<TrackDto>

    @GET("/tracks/search")
    suspend fun searchTracks(@Query("q") query: String): List<TrackDto>

    @GET("/playlists")
    suspend fun getPlaylists(): List<PlaylistDto>

    @POST("/playlists")
    suspend fun createPlaylist(@Body request: CreatePlaylistRequest): PlaylistDto

    @PUT("/playlists/{id}")
    suspend fun updatePlaylist(@Path("id") id: Int, @Body request: UpdatePlaylistRequest): Response<Unit>

    @DELETE("/playlists/{id}")
    suspend fun deletePlaylist(@Path("id") id: Int): Response<Unit>

    @POST("/playlists/{id}/tracks")
    suspend fun addTrackToPlaylist(@Path("id") playlistId: Int, @Body request: AddTrackRequest): Response<Unit>

    @DELETE("/playlists/{id}/tracks/{trackId}")
    suspend fun removeTrackFromPlaylist(@Path("id") playlistId: Int, @Path("trackId") trackId: Int): Response<Unit>
}