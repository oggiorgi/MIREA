package com.example.museflow.presentation.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.usecase.AddTrackToPlaylistUseCase
import com.example.museflow.domain.usecase.CreatePlaylistUseCase
import com.example.museflow.domain.usecase.DeletePlaylistUseCase
import com.example.museflow.domain.usecase.GetPlaylistsUseCase
import com.example.museflow.domain.usecase.GetTracksUseCase
import com.example.museflow.domain.usecase.SearchTracksUseCase
import com.example.museflow.presentation.ui.catalog.CatalogScreen
import com.example.museflow.presentation.ui.catalog.CatalogViewModel
import com.example.museflow.presentation.ui.catalog.CatalogViewModelFactory
import com.example.museflow.presentation.ui.playlists.PlaylistsScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsState
import com.example.museflow.presentation.ui.playlists.PlaylistsViewModel
import com.example.museflow.presentation.ui.playlists.PlaylistsViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onNavigateToPlayer: (Track, List<Track>) -> Unit,
    getTracksUseCase: GetTracksUseCase,
    searchTracksUseCase: SearchTracksUseCase,
    getPlaylistsUseCase: GetPlaylistsUseCase,
    createPlaylistUseCase: CreatePlaylistUseCase,
    deletePlaylistUseCase: DeletePlaylistUseCase,
    addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase,
    coroutineScope: CoroutineScope
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Создаём ViewModel с фабриками
    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModelFactory(getTracksUseCase, searchTracksUseCase)
    )

    val playlistsViewModel: PlaylistsViewModel = viewModel(
        factory = PlaylistsViewModelFactory(getPlaylistsUseCase, createPlaylistUseCase, deletePlaylistUseCase)
    )

    // Получаем список плейлистов из состояния
    val playlistsState by playlistsViewModel.state.collectAsState()
    val playlists = if (playlistsState is PlaylistsState.Success) {
        (playlistsState as PlaylistsState.Success).playlists
    } else {
        emptyList()
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("catalog") },
                    label = { Text("Каталог") },
                    icon = { Icon(Icons.Default.MusicNote, null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("playlists") },
                    label = { Text("Плейлисты") },
                    icon = { Icon(Icons.Default.PlaylistPlay, null) }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "catalog",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("catalog") {
                CatalogScreen(
                    onTrackClick = { track ->
                        onNavigateToPlayer(track, listOf(track))
                    },
                    playlists = playlists,
                    onAddToPlaylist = { playlistId, trackId ->
                        coroutineScope.launch {
                            try {
                                val added = addTrackToPlaylistUseCase(playlistId, trackId)
                                if (added) {
                                    playlistsViewModel.loadPlaylists()
                                    Toast.makeText(
                                        context,
                                        "Трек добавлен в плейлист",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Трек уже есть в этом плейлисте",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Ошибка: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    viewModel = catalogViewModel
                )
            }
            composable("playlists") {
                PlaylistsScreen(
                    onPlaylistClick = { playlist ->
                        if (playlist.tracks.isNotEmpty()) {
                            onNavigateToPlayer(playlist.tracks.first(), playlist.tracks)
                        }
                    },
                    viewModel = playlistsViewModel
                )
            }
        }
    }
}

// ==================== PREVIEWS ====================

// Preview для MainScreen (используем статические данные)
@Preview(showBackground = true, name = "Main Screen Preview")
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Создаем упрощенную версию для preview
            val navController = rememberNavController()

            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            selected = false,
                            onClick = { },
                            label = { Text("Каталог") },
                            icon = { Icon(Icons.Default.MusicNote, null) }
                        )
                        NavigationBarItem(
                            selected = false,
                            onClick = { },
                            label = { Text("Плейлисты") },
                            icon = { Icon(Icons.Default.PlaylistPlay, null) }
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = "catalog",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("catalog") {
                        // Упрощенный CatalogScreen для preview
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Экран каталога (preview)")
                        }
                    }
                    composable("playlists") {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Экран плейлистов (preview)")
                        }
                    }
                }
            }
        }
    }
}