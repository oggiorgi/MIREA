package com.example.photocatalog.domain.repository

import com.example.photocatalog.domain.entity.Photo

interface PhotoRepository {
    suspend fun getPhotos(): Result<List<Photo>>
    suspend fun downloadPhoto(url: String): Result<ByteArray>
}