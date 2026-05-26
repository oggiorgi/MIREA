package com.example.museflow.domain.repository

import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.models.Track

interface PlaylistsRepository {
    suspend fun getPlaylists(): List<Playlist>
    suspend fun createPlaylist(name: String, coverUrl: String?): Playlist
    suspend fun updatePlaylist(playlistId: Int, newName: String)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun addTrackToPlaylist(playlistId: Int, trackId: Int)
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: Int)
}