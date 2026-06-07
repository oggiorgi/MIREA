package com.example.museflow.di

import com.example.museflow.data.local.dao.TrackDao
import com.example.museflow.data.repository.AuthRepositoryImpl
import com.example.museflow.data.repository.PlaylistsRepositoryImpl
import com.example.museflow.data.repository.TracksRepositoryImpl
import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.domain.repository.AuthRepository
import com.example.museflow.domain.repository.PlaylistsRepository
import com.example.museflow.domain.repository.TracksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
 * Модуль внедрения зависимостей для репозиториев. 
 * Связывает интерфейсы доменного слоя с их конкретными реализациями из слоя данных.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: ApiService,
        tokenManager: TokenManager
    ): AuthRepository {
        return AuthRepositoryImpl(api, tokenManager)
    }

    @Provides
    @Singleton
    fun provideTracksRepository(
        api: ApiService,
        trackDao: TrackDao
    ): TracksRepository {
        return TracksRepositoryImpl(api, trackDao)
    }

    @Provides
    @Singleton
    fun providePlaylistsRepository(api: ApiService): PlaylistsRepository {
        return PlaylistsRepositoryImpl(api)
    }
}