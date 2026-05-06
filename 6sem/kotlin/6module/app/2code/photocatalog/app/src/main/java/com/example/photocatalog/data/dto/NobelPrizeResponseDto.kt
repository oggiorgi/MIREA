package com.example.photocatalog.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NobelPrizeResponseDto(
    val nobelPrizes: List<NobelPrizeDto>
)

@Serializable
data class NobelPrizeDto(
    val awardYear: String,
    val category: CategoryDto,
    val laureates: List<LaureateDto>?
)

@Serializable
data class CategoryDto(
    val en: String
)

@Serializable
data class LaureateDto(
    val id: String? = null,
    val knownName: KnownNameDto? = null,
    val fullName: FullNameDto? = null,
    val portion: String? = null,
    val sortOrder: String? = null,
    val motivation: MotivationDto? = null,
    val links: List<LinkDto>? = null
)

@Serializable
data class KnownNameDto(
    val en: String? = null
)

@Serializable
data class FullNameDto(
    val en: String? = null
)

@Serializable
data class MotivationDto(
    val en: String? = null
)

@Serializable
data class LinkDto(
    val rel: String? = null,
    val href: String? = null,
    val action: String? = null,
    val types: String? = null
)

// Эти классы пока не используются, но оставим на будущее
@Serializable
data class BirthInfoDto(
    val place: PlaceDto? = null,
    val country: CountryDto? = null
)

@Serializable
data class PlaceDto(
    val city: String? = null,
    val country: CountryDto? = null
)

@Serializable
data class CountryDto(
    val en: String? = null
)