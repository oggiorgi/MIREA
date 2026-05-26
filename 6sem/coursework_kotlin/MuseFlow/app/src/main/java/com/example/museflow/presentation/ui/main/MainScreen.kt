package com.example.museflow.presentation.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.museflow.domain.models.Track
import com.example.museflow.presentation.ui.catalog.CatalogScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment

@Composable
fun MainScreen(
    onNavigateToPlayer: (Track, List<Track>) -> Unit
) {
    val navController = rememberNavController()
    var selectedPlaylistTracks by remember { mutableStateOf<List<Track>>(emptyList()) }

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
                    playlists = emptyList(),
                    onAddToPlaylist = { playlistId, trackId ->
                        // Будет реализовано позже
                    }
                )
            }
            composable("playlists") {
                PlaylistsScreen(
                    onPlaylistClick = { playlist ->
                        if (playlist.tracks.isNotEmpty()) {
                            selectedPlaylistTracks = playlist.tracks
                            onNavigateToPlayer(playlist.tracks.first(), playlist.tracks)
                        }
                    }
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