package com.example.museflow.data.network.models

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class LoginRequest(val login: String, val password: String)

@OptIn(InternalSerializationApi::class)
@Serializable
data class RegisterRequest(val login: String, val email: String, val password: String)

@OptIn(InternalSerializationApi::class)
@Serializable
data class AuthResponse(
    @SerialName("token")
    val token: String? = null,
    @SerialName("message")
    val message: String? = null
)