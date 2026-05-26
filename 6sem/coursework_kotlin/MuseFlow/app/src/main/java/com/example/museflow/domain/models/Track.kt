package com.example.museflow.domain.models

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    val coverUrl: String,
    val audioUrl: String,
    val genre: String?
)