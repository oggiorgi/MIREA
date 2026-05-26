package com.example.museflow.presentation.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.usecase.CreatePlaylistUseCase
import com.example.museflow.domain.usecase.DeletePlaylistUseCase
import com.example.museflow.domain.usecase.GetPlaylistsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlaylistsState {
    object Loading : PlaylistsState()
    data class Success(val playlists: List<Playlist>) : PlaylistsState()
    data class Error(val message: String) : PlaylistsState()
}

class PlaylistsViewModel(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<PlaylistsState>(PlaylistsState.Loading)
    val state: StateFlow<PlaylistsState> = _state.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating.asStateFlow()

    init {
        loadPlaylists()
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            _state.value = PlaylistsState.Loading
            try {
                val playlists = getPlaylistsUseCase()
                _state.value = PlaylistsState.Success(playlists)
            } catch (e: Exception) {
                _state.value = PlaylistsState.Error(e.message ?: "Ошибка загрузки плейлистов")
            }
        }
    }

    fun createPlaylist(name: String, coverUrl: String? = null) {
        viewModelScope.launch {
            _isCreating.value = true
            try {
                createPlaylistUseCase(name, coverUrl)
                loadPlaylists() // Перезагружаем список
            } catch (e: Exception) {
                _state.value = PlaylistsState.Error(e.message ?: "Ошибка создания плейлиста")
            } finally {
                _isCreating.value = false
            }
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            try {
                deletePlaylistUseCase(playlistId)
                loadPlaylists()
            } catch (e: Exception) {
                _state.value = PlaylistsState.Error(e.message ?: "Ошибка удаления плейлиста")
            }
        }
    }
}