package org.example.database.playlists

import kotlinx.serialization.Serializable
import org.example.database.tracks.TrackDTO

@Serializable
data class PlaylistDTO(
    val id: Int,
    val userId: Int,
    val name: String,
    val coverUrl: String?,
    val createdAt: String,
    val tracks: List<TrackDTO> = emptyList()
)