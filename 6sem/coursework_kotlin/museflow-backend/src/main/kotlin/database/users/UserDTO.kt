package org.example.database.users

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int = 0,
    val login: String,
    val email: String,
    val passwordHash: String,
    val createdAt: String? = null
)