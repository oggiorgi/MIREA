package com.example.photocatalog.data.repository

import com.example.photocatalog.data.api.PhotoApiService
import com.example.photocatalog.domain.entity.Photo
import com.example.photocatalog.domain.repository.PhotoRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class PhotoRepositoryImpl : PhotoRepository {
    private val apiService: PhotoApiService
    private val okHttpClient: OkHttpClient

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dog.ceo/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        apiService = retrofit.create(PhotoApiService::class.java)
    }

    override suspend fun getPhotos(): Result<List<Photo>> {
        return try {
            val photos = mutableListOf<Photo>()

            // Делаем * запросов для получения * фото
            for (i in 0 until 4) {
                val response = apiService.getRandomPhoto()
                if (response.status == "success") {
                    val breed = extractBreedFromUrl(response.message)

                    photos.add(
                        Photo(
                            id = "dog_$i",
                            author = breed,
                            width = 800,  // Значение по умолчанию
                            height = 600, // Значение по умолчанию
                            url = response.message,
                            downloadUrl = response.message
                        )
                    )
                }
            }

            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadPhoto(url: String): Result<ByteArray> {
        return try {
            val request = okhttp3.Request.Builder()
                .url(url)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success(response.body?.bytes() ?: ByteArray(0))
            } else {
                Result.failure(Exception("Failed to download: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractBreedFromUrl(url: String): String {
        return try {
            val breedPart = url.split("/breeds/")[1].split("/")[0]
            breedPart.replace("-", " ").split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
        } catch (e: Exception) {
            "Beautiful Dog"
        }
    }
}