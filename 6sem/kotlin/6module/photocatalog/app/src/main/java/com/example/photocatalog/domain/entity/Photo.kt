package com.example.photocatalog.domain.entity

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String
)