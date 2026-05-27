package com.example.museflow.presentation.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.usecase.GetTracksUseCase
import com.example.museflow.domain.usecase.SearchTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CatalogState {
    object Loading : CatalogState()
    data class Success(val tracks: List<Track>) : CatalogState()
    data class Error(val message: String) : CatalogState()
}

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getTracksUseCase: GetTracksUseCase,
    private val searchTracksUseCase: SearchTracksUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<CatalogState>(CatalogState.Loading)
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    private var allTracks: List<Track> = emptyList()

    init {
        loadTracks()
    }

    fun loadTracks() {
        viewModelScope.launch {
            _state.value = CatalogState.Loading
            try {
                allTracks = getTracksUseCase()
                _state.value = CatalogState.Success(allTracks)
            } catch (e: Exception) {
                _state.value = CatalogState.Error(e.message ?: "Ошибка загрузки треков")
            }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _state.value = CatalogState.Success(allTracks)
            return
        }
        viewModelScope.launch {
            _state.value = CatalogState.Loading
            try {
                val results = searchTracksUseCase(query)
                _state.value = CatalogState.Success(results)
            } catch (e: Exception) {
                _state.value = CatalogState.Error(e.message ?: "Ошибка поиска")
            }
        }
    }
}