package com.example.photocatalog.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val image: String,
    val accessToken: String
) {
    val token: String get() = accessToken  // Вычисляемое свойство для совместимости
}