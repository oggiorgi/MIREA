package com.example.photocatalog.domain.model

data class Laureate(
    val id: String,
    val name: String,
    val year: String,
    val category: String,
    val motivation: String,
    val country: String,
    val portraitUrl: String?,
    val portion: String? = null
)