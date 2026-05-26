package com.example.museflow.presentation.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.museflow.domain.usecase.GetTracksUseCase
import com.example.museflow.domain.usecase.SearchTracksUseCase

class CatalogViewModelFactory(
    private val getTracksUseCase: GetTracksUseCase,
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(getTracksUseCase, searchTracksUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}