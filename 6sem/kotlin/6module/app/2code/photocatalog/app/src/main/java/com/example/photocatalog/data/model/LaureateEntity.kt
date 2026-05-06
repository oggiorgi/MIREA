package com.example.photocatalog.data.model

data class LaureateEntity(
    val id: String,
    val name: String,
    val year: String,
    val category: String,
    val motivation: String,
    val country: String,
    val portraitUrl: String?,
    val portion: String?
)