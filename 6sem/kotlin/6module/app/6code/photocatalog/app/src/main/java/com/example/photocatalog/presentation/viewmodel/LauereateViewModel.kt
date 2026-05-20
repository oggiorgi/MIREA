package com.example.photocatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.data.local.TokenRepository
import com.example.photocatalog.domain.model.Laureate
import com.example.photocatalog.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val laureates: List<Laureate>) : UiState()
    data class Error(val message: String) : UiState()
}

class LaureateViewModel(
    private val getLaureatesUseCase: GetLaureatesUseCase,
    private val filterLaureatesUseCase: FilterLaureatesUseCase,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var allLaureates = listOf<Laureate>()
    private val _selectedYear = MutableStateFlow<String?>(null)
    private val _selectedCategory = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            tokenRepository.getTokenFlow().collect { token ->
                if (!token.isNullOrEmpty()) {
                    loadLaureates()
                }
            }
        }
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

    fun logout() {
        viewModelScope.launch {
            tokenRepository.clearToken()
            _uiState.value = UiState.Loading
            allLaureates = emptyList()
        }
    }
}