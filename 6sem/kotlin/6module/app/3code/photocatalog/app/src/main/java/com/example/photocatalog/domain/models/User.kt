package com.example.photocatalog.domain.models

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val image: String
)