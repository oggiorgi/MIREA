package com.example.firstapplication.Data

import kotlinx.serialization.Serializable

@Serializable
data class Repo(
    val id: Long,
    val full_name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?
)