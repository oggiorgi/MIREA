package com.example.museflow.presentation.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.museflow.domain.usecase.CreatePlaylistUseCase
import com.example.museflow.domain.usecase.DeletePlaylistUseCase
import com.example.museflow.domain.usecase.GetPlaylistsUseCase

class PlaylistsViewModelFactory(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlaylistsViewModel(getPlaylistsUseCase, createPlaylistUseCase, deletePlaylistUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}