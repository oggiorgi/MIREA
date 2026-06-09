package com.example.museflow.data.network.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class PlaylistDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val coverUrl: String?,
    val createdAt: String,
    val tracks: List<TrackDto>
)