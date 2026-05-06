package com.example.photocatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photocatalog.domain.usecase.FilterLaureatesUseCase
import com.example.photocatalog.domain.usecase.GetLaureatesUseCase  // ← исправлено: usecase

class LaureateViewModelFactory(
    private val getLaureatesUseCase: GetLaureatesUseCase,
    private val filterLaureatesUseCase: FilterLaureatesUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaureateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LaureateViewModel(
                getLaureatesUseCase = getLaureatesUseCase,
                filterLaureatesUseCase = filterLaureatesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}