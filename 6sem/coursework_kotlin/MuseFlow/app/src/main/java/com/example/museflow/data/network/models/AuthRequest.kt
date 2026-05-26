package com.example.museflow.data.network.models


import com.google.gson.annotations.SerializedName
data class LoginRequest(val login: String, val password: String)
data class RegisterRequest(val login: String, val email: String, val password: String)

data class AuthResponse(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("message")
    val message: String? = null
)