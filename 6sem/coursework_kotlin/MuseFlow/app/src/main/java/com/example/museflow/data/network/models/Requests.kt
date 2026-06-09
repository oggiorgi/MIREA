package com.example.museflow.data.network.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class CreatePlaylistRequest(val name: String, val coverUrl: String? = null)

@OptIn(InternalSerializationApi::class)
@Serializable
data class UpdatePlaylistRequest(val name: String)

@OptIn(InternalSerializationApi::class)
@Serializable
data class AddTrackRequest(val trackId: Int)