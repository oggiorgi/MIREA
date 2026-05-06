package com.example.photocatalog.domain.usercase

import com.example.photocatalog.domain.entity.Photo
import com.example.photocatalog.domain.repository.PhotoRepository

class GetPhotosUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(): Result<List<Photo>> {
        return try {
            repository.getPhotos()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}