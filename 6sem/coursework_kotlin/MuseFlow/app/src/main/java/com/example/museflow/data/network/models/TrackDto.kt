package com.example.museflow.data.network.models

data class TrackDto(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    val coverUrl: String,
    val audioUrl: String,
    val genre: String?
)