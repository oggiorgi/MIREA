package com.example.photocatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.domain.model.Laureate  // ← domain model
import com.example.photocatalog.domain.usecase.FilterLaureatesUseCase
import com.example.photocatalog.domain.usecase.GetLaureatesUseCase  // ← исправлено: usecase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val laureates: List<Laureate>) : UiState()
    data class Error(val message: String) : UiState()
}

class LaureateViewModel(
    private val getLaureatesUseCase: GetLaureatesUseCase,
    private val filterLaureatesUseCase: FilterLaureatesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var allLaureates = listOf<Laureate>()
    private val _selectedYear = MutableStateFlow<String?>(null)
    private val _selectedCategory = MutableStateFlow<String?>(null)

    init {
        loadLaureates()
    }

    fun loadLaureates() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                allLaureates = getLaureatesUseCase()
                applyFilters()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun filterByYear(year: String?) {
        _selectedYear.value = year
        applyFilters()
    }

    fun filterByCategory(category: String?) {
        _selectedCategory.value = category
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = filterLaureatesUseCase(
            allLaureates,
            _selectedYear.value,
            _selectedCategory.value
        )
        _uiState.value = UiState.Success(filtered)
    }
}