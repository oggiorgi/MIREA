package com.example.photocatalog.data.mappers

import com.example.photocatalog.data.remote.dto.UserDto
import com.example.photocatalog.domain.models.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        username = username,
        email = email,
        image = image
    )
}

fun List<UserDto>.toDomain(): List<User> {
    return this.map { it.toDomain() }
}