package com.example.museflow.data.network.models

data class PlaylistDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val coverUrl: String?,
    val createdAt: String,
    val tracks: List<TrackDto>
)