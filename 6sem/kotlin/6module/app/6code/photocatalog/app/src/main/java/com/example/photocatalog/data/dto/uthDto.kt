package com.example.photocatalog.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(val login: String, val password: String)

@Serializable
data class LoginResponseDto(val token: String)

@Serializable
data class RegisterRequestDto(val login: String, val email: String, val password: String)

@Serializable
data class RegisterResponseDto(
    val token: String? = null,
    val information: String? = null
)
