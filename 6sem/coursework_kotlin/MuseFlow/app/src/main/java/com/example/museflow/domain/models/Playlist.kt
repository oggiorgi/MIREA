package com.example.museflow.domain.models

data class Playlist(
    val id: Int,
    val name: String,
    val coverUrl: String?,
    val tracks: List<Track>
)