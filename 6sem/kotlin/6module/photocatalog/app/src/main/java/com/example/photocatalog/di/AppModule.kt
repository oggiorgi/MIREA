package com.example.photocatalog.di

import com.example.photocatalog.data.repository.PhotoRepositoryImpl
import com.example.photocatalog.domain.repository.PhotoRepository
import com.example.photocatalog.domain.usercase.GetPhotosUseCase

object AppModule {
    private val photoRepository: PhotoRepository by lazy {
        PhotoRepositoryImpl()
    }

    val getPhotosUseCase: GetPhotosUseCase by lazy {
        GetPhotosUseCase(photoRepository)
    }
}