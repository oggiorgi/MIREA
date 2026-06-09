package com.example.museflow.di

import android.content.Context
import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.api.KtorApiService
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.data.network.client.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

/*
 * Модуль внедрения зависимостей для сетевого слоя. 
 * Настраивает механизмы авторизации и API-сервисы.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideHttpClient(tokenManager: TokenManager): HttpClient {
        return KtorClient.provideHttpClient(tokenManager)
    }

    @Provides
    @Singleton
    fun provideApiService(
        client: HttpClient
    ): ApiService {
        return KtorApiService(client)
    }
}