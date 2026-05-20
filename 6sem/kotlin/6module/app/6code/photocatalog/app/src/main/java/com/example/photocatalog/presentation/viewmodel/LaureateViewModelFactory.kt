package com.example.photocatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photocatalog.data.local.TokenRepository
import com.example.photocatalog.domain.usecase.*

class LaureateViewModelFactory(
    private val getLaureatesUseCase: GetLaureatesUseCase,
    private val filterLaureatesUseCase: FilterLaureatesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val tokenRepository: TokenRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaureateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LaureateViewModel(
                getLaureatesUseCase,
                filterLaureatesUseCase,
                addFavoriteUseCase,
                removeFavoriteUseCase,
                getFavoritesUseCase,
                tokenRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}