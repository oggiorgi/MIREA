package com.example.photocatalog.presentation.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.domain.entity.Photo
import com.example.photocatalog.domain.usercase.GetPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PhotoListState {
    object Loading : PhotoListState()
    data class Success(val photos: List<Photo>) : PhotoListState()
    data class Error(val message: String) : PhotoListState()
}

class PhotoListViewModel(
    private val getPhotosUseCase: GetPhotosUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<PhotoListState>(PhotoListState.Loading)
    val state: StateFlow<PhotoListState> = _state.asStateFlow()

    init {
        loadPhotos()
    }

    fun loadPhotos() {
        viewModelScope.launch {
            _state.value = PhotoListState.Loading
            val result = getPhotosUseCase()
            result.fold(
                onSuccess = { photos ->
                    _state.value = PhotoListState.Success(photos)
                },
                onFailure = { exception ->
                    _state.value = PhotoListState.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }
}