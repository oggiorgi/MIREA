package com.example.museflow.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.museflow.domain.models.Track

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val artist: String,
    val duration: Int,
    val coverUrl: String,
    val audioUrl: String,
    val genre: String?
)

// Extension function для маппинга
fun TrackEntity.toDomain(): Track = Track(
    id = id,
    title = title,
    artist = artist,
    duration = duration,
    coverUrl = coverUrl,
    audioUrl = audioUrl,
    genre = genre
)

fun Track.toEntity(): TrackEntity = TrackEntity(
    id = id,
    title = title,
    artist = artist,
    duration = duration,
    coverUrl = coverUrl,
    audioUrl = audioUrl,
    genre = genre
)