package com.example.museflow.data.network.client

import android.content.Context
import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/*
 * Фабрика сетевого клиента. 
 * Настраивает Retrofit и OkHttpClient для работы с REST API, включая 
 * логирование и автоматическую обработку JWT токенов.
 */
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    fun provideApiService(tokenManager: TokenManager, context: Context): ApiService {
        /*
         * Интерцептор для автоматического добавления JWT токена в заголовок Authorization.
         * Это избавляет от необходимости вручную передавать токен в каждом методе ApiService.
         */
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
            tokenManager.getToken()?.let { token ->
                request.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(request.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}