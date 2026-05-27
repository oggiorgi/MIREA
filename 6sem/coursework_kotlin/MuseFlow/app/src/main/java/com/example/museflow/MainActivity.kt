package com.example.museflow

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

        // Загружаем сохранённую тему
        val sharedPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        setContent {
            // Состояние темы для переключения (вынесено внутрь setContent)
            var isDarkTheme by remember { mutableStateOf(sharedPrefs.getBoolean("dark_theme", false)) }

            // Функция для переключения темы
            val onThemeToggle = {
                isDarkTheme = !isDarkTheme
                sharedPrefs.edit().putBoolean("dark_theme", isDarkTheme).apply()
            }

            MuseFlowTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                // Получаем ViewModel через Hilt
                val catalogViewModel: CatalogViewModel = viewModel()

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
}