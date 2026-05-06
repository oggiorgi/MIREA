package com.example.photocatalog.data.api

import com.example.photocatalog.data.dto.PhotoDto
import retrofit2.http.GET

interface PhotoApiService {
    @GET("api/breeds/image/random")
    suspend fun getRandomPhoto(): PhotoDto
}