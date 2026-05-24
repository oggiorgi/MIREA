package org.example.database.playlists

import org.example.database.tracks.TrackDTO
import java.time.LocalDateTime

data class PlaylistDTO(
    val id: Int,
    val userId: Int,
    val name: String,
    val coverUrl: String?,
    val createdAt: String,
    val tracks: List<TrackDTO> = emptyList()
)