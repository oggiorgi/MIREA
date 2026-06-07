package com.example.museflow.di

import com.example.museflow.domain.repository.AuthRepository
import com.example.museflow.domain.repository.PlaylistsRepository
import com.example.museflow.domain.repository.TracksRepository
import com.example.museflow.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
 * Модуль внедрения зависимостей для бизнес-логики. 
 * Предоставляет UseCase объекты для использования во ViewModels, следуя Clean Architecture.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTracksUseCase(repository: TracksRepository): GetTracksUseCase {
        return GetTracksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchTracksUseCase(repository: TracksRepository): SearchTracksUseCase {
        return SearchTracksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPlaylistsUseCase(repository: PlaylistsRepository): GetPlaylistsUseCase {
        return GetPlaylistsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreatePlaylistUseCase(repository: PlaylistsRepository): CreatePlaylistUseCase {
        return CreatePlaylistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeletePlaylistUseCase(repository: PlaylistsRepository): DeletePlaylistUseCase {
        return DeletePlaylistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddTrackToPlaylistUseCase(repository: PlaylistsRepository): AddTrackToPlaylistUseCase {
        return AddTrackToPlaylistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoveTrackFromPlaylistUseCase(repository: PlaylistsRepository): RemoveTrackFromPlaylistUseCase {
        return RemoveTrackFromPlaylistUseCase(repository)
    }
}