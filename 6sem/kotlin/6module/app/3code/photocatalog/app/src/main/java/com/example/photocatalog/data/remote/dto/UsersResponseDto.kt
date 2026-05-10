package com.example.photocatalog.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsersResponseDto(
    val users: List<UserDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)