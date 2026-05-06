package com.example.photocatalog.data.dto

import com.squareup.moshi.Json

data class PhotoDto(
    @Json(name = "message")
    val message: String,
    @Json(name = "status")
    val status: String
)