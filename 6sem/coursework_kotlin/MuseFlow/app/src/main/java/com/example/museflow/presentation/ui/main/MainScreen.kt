package com.example.museflow.presentation.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.domain.models.Track
import com.example.museflow.presentation.ui.catalog.CatalogScreen
import com.example.museflow.presentation.ui.catalog.CatalogViewModel
import com.example.museflow.presentation.ui.playlists.PlaylistsScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsState
import com.example.museflow.presentation.ui.playlists.PlaylistsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    onNavigateToPlayer: (Track, List<Track>) -> Unit,
    coroutineScope: CoroutineScope,
    tokenManager: TokenManager,
    onLogout: () -> Unit,
    onClearCache: () -> Unit  // ← добавить параметр
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val catalogViewModel: CatalogViewModel = hiltViewModel()
    val playlistsViewModel: PlaylistsViewModel = hiltViewModel()

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
                    icon = { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, null) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profile") },
                    label = { Text("Профиль") },
                    icon = { Icon(Icons.Default.Person, null) }
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
                                val added = playlistsViewModel.addTrackToPlaylist(playlistId, trackId)
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
            composable("profile") {
                ProfileScreen(
                    tokenManager = tokenManager,
                    onLogout = onLogout,
                    onClearCache = onClearCache  // ← передать параметр
                )
            }
        }
    }
}