package com.example.museflow.data.network.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class TrackDto(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    val coverUrl: String,
    val audioUrl: String,
    val genre: String?
)