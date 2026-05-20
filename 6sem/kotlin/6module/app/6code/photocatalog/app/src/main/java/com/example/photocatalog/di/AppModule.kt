package com.example.photocatalog.di

import android.content.Context
import com.example.photocatalog.data.local.TokenRepository
import com.example.photocatalog.data.network.ApiService
import com.example.photocatalog.data.network.KtorClient
import com.example.photocatalog.data.repository.NobelPrizeRepositoryImpl
import com.example.photocatalog.domain.repository.NobelPrizeRepository
import com.example.photocatalog.domain.usecase.*
import com.example.photocatalog.presentation.viewmodel.AuthViewModel
import com.example.photocatalog.presentation.viewmodel.LaureateViewModel

object AppModule {
    private lateinit var context: Context

    fun init(ctx: Context) {
        context = ctx
    }

    fun provideTokenRepository(): TokenRepository = TokenRepository(context)

    fun provideApiService(): ApiService {
        val client = KtorClient.createClient(context)
        return ApiService(client, provideTokenRepository())
    }

    fun provideRepository(): NobelPrizeRepository {
        return NobelPrizeRepositoryImpl(provideApiService())
    }

    fun provideGetLaureatesUseCase(): GetLaureatesUseCase {
        return GetLaureatesUseCase(provideRepository())
    }

    fun provideFilterLaureatesUseCase(): FilterLaureatesUseCase {
        return FilterLaureatesUseCase()
    }

    fun provideLoginUseCase(): LoginUseCase {
        return LoginUseCase(provideRepository())
    }

    fun provideRegisterUseCase(): RegisterUseCase {
        return RegisterUseCase(provideRepository())
    }

    fun provideAddFavoriteUseCase(): AddFavoriteUseCase {
        return AddFavoriteUseCase(provideRepository())
    }

    fun provideRemoveFavoriteUseCase(): RemoveFavoriteUseCase {
        return RemoveFavoriteUseCase(provideRepository())
    }

    fun provideGetFavoritesUseCase(): GetFavoritesUseCase {
        return GetFavoritesUseCase(provideRepository())
    }

    fun provideLaureateViewModel(): LaureateViewModel {
        return LaureateViewModel(
            provideGetLaureatesUseCase(),
            provideFilterLaureatesUseCase(),
            provideAddFavoriteUseCase(),
            provideRemoveFavoriteUseCase(),
            provideGetFavoritesUseCase(),
            provideTokenRepository()
        )
    }

    fun provideAuthViewModel(): AuthViewModel {
        return AuthViewModel(provideLoginUseCase(), provideRegisterUseCase())
    }
}