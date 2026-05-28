package com.example.museflow

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.TracksRepository
import com.example.museflow.presentation.ui.auth.AuthScreen
import com.example.museflow.presentation.ui.auth.AuthViewModel
import com.example.museflow.presentation.ui.catalog.CatalogViewModel
import com.example.museflow.presentation.ui.genre.GenreTracksScreen
import com.example.museflow.presentation.ui.main.MainScreen
import com.example.museflow.presentation.ui.player.PlayerScreen
import com.example.museflow.presentation.ui.playlists.PlaylistDetailScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsState
import com.example.museflow.presentation.ui.playlists.PlaylistsViewModel
import com.example.museflow.services.PlaybackService
import com.example.museflow.ui.theme.MuseFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var tracksRepository: TracksRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Запрос разрешения на уведомления
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContent {
            var isDarkTheme by remember { mutableStateOf(sharedPrefs.getBoolean("dark_theme", false)) }

            val onThemeToggle = {
                isDarkTheme = !isDarkTheme
                sharedPrefs.edit().putBoolean("dark_theme", isDarkTheme).apply()
            }

            MuseFlowTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                val catalogViewModel: CatalogViewModel = viewModel()
                val playlistsViewModel: PlaylistsViewModel = viewModel()

                val savedToken = remember { tokenManager.getToken() }
                var isAuthenticated by remember {
                    mutableStateOf(savedToken != null && savedToken!!.isNotEmpty())
                }
                var currentTrack by remember { mutableStateOf<Track?>(null) }
                var playlistTracks by remember { mutableStateOf<List<Track>>(emptyList()) }

                NavHost(
                    navController = navController,
                    startDestination = if (isAuthenticated) "main" else "auth",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("auth") {
                        val authViewModel: AuthViewModel = viewModel()
                        AuthScreen(
                            authViewModel = authViewModel,
                            onSuccess = { token ->
                                isAuthenticated = true
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        MainScreen(
                            onNavigateToPlayer = { track, tracks ->
                                currentTrack = track
                                playlistTracks = tracks
                                navController.navigate("player/${track.id}")
                            },
                            onNavigateToPlaylist = { playlistId ->
                                navController.navigate("playlist/$playlistId")
                            },
                            coroutineScope = coroutineScope,
                            tokenManager = tokenManager,
                            onLogout = {
                                tokenManager.clearToken()
                                isAuthenticated = false
                                navController.navigate("auth") {
                                    popUpTo("main") { inclusive = true }
                                }
                            },
                            onClearCache = {
                                coroutineScope.launch {
                                    tracksRepository.clearCache()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Кэш очищен",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            isDarkTheme = isDarkTheme,
                            onThemeToggle = onThemeToggle
                        )
                    }

                    // ✅ ПРАВИЛЬНОЕ МЕСТО ДЛЯ playlist - на том же уровне, что и main
                    composable("playlist/{playlistId}") { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getString("playlistId")?.toIntOrNull()
                        val playlistsState by playlistsViewModel.state.collectAsState()
                        val playlist = if (playlistsState is PlaylistsState.Success) {
                            (playlistsState as PlaylistsState.Success).playlists.find { it.id == playlistId }
                        } else null

                        if (playlist != null) {
                            PlaylistDetailScreen(
                                playlist = playlist,
                                onTrackClick = { track ->
                                    currentTrack = track
                                    playlistTracks = playlist.tracks
                                    navController.navigate("player/${track.id}")
                                },
                                onRemoveTrack = { trackId ->
                                    coroutineScope.launch {
                                        playlistsViewModel.removeTrackFromPlaylist(playlistId!!, trackId)
                                        playlistsViewModel.loadPlaylists()
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Трек удалён из плейлиста",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                onBack = { navController.popBackStack() }
                            )
                        } else {
                            navController.popBackStack()
                        }
                    }

                    composable("genre/{genreName}") { backStackEntry ->
                        val genreName = backStackEntry.arguments?.getString("genreName") ?: ""
                        val tracks = catalogViewModel.getTracksByGenre()[genreName] ?: emptyList()

                        GenreTracksScreen(
                            genreName = genreName,
                            tracks = tracks,
                            onTrackClick = { track ->
                                currentTrack = track
                                playlistTracks = tracks
                                navController.navigate("player/${track.id}")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("player/{trackId}") { backStackEntry ->
                        val trackId = backStackEntry.arguments?.getString("trackId")?.toIntOrNull()
                        val track = currentTrack ?: playlistTracks.find { it.id == trackId }

                        if (track != null) {
                            PlayerScreen(
                                track = track,
                                playlistTracks = playlistTracks,
                                onNext = {
                                    val currentIndex = playlistTracks.indexOfFirst { it.id == track.id }
                                    if (currentIndex + 1 < playlistTracks.size) {
                                        currentTrack = playlistTracks[currentIndex + 1]
                                        navController.popBackStack()
                                        navController.navigate("player/${currentTrack?.id}")
                                    }
                                },
                                onPrevious = {
                                    val currentIndex = playlistTracks.indexOfFirst { it.id == track.id }
                                    if (currentIndex - 1 >= 0) {
                                        currentTrack = playlistTracks[currentIndex - 1]
                                        navController.popBackStack()
                                        navController.navigate("player/${currentTrack?.id}")
                                    }
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }

    // Останавливаем сервис при закрытии приложения
    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, PlaybackService::class.java)
        stopService(intent)
    }
}