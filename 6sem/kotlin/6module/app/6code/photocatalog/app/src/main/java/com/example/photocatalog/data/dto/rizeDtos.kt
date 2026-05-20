package com.example.photocatalog.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PrizeSummaryDto(
    val year: Int,
    val category: String,
    val laureatesCount: Int
)

@Serializable
data class PrizeDetailDto(
    val year: Int,
    val category: String,
    val laureates: List<LaureateResponseDto>
)

@Serializable
data class LaureateResponseDto(
    val id: String,
    val name: String,
    val motivation: String,
    val share: Int
)

