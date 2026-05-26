package com.example.museflow.data.network.models

data class CreatePlaylistRequest(val name: String, val coverUrl: String? = null)
data class UpdatePlaylistRequest(val name: String)
data class AddTrackRequest(val trackId: Int)