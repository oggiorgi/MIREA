package org.example.database.tracks

import kotlinx.serialization.Serializable

@Serializable
data class TrackDTO(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    val coverUrl: String,
    val audioUrl: String,
    val genre: String? = null
)