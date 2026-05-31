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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.TracksRepository
import com.example.museflow.presentation.ui.auth.AuthScreen
import com.example.museflow.presentation.ui.auth.AuthViewModel
import com.example.museflow.presentation.ui.catalog.CatalogViewModel
import com.example.museflow.presentation.ui.main.MainScreen
import com.example.museflow.presentation.ui.player.PlayerScreen
import com.example.museflow.presentation.ui.playlists.PlaylistDetailScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsState
import com.example.museflow.presentation.ui.playlists.PlaylistsViewModel
import com.example.museflow.services.PlaybackService
import com.example.museflow.ui.theme.MuseFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
                val rootNavController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                val catalogViewModel: CatalogViewModel = hiltViewModel()
                val playlistsViewModel: PlaylistsViewModel = hiltViewModel()

                val savedToken = remember { tokenManager.getToken() }
                var isAuthenticated by remember {
                    mutableStateOf(savedToken != null && savedToken!!.isNotEmpty())
                }
                var currentTrack by remember { mutableStateOf<Track?>(null) }
                var playlistTracks by remember { mutableStateOf<List<Track>>(emptyList()) }

                NavHost(
                    navController = rootNavController,
                    startDestination = if (isAuthenticated) "main" else "auth",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("auth") {
                        val authViewModel: AuthViewModel = hiltViewModel()
                        AuthScreen(
                            authViewModel = authViewModel,
                            onSuccess = { token ->
                                isAuthenticated = true
                                // Принудительная загрузка данных после входа
                                catalogViewModel.loadTracks()
                                playlistsViewModel.loadPlaylists()
                                
                                rootNavController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("main") {
                        key(isAuthenticated) {
                            MainScreen(
                                onNavigateToPlayer = { track, tracks ->
                                    currentTrack = track
                                    playlistTracks = tracks
                                    rootNavController.navigate("player/${track.id}")
                                },
                                onNavigateToPlaylist = { playlistId ->
                                    rootNavController.navigate("playlist/$playlistId")
                                },
                                coroutineScope = coroutineScope,
                                tokenManager = tokenManager,
                                onLogout = {
                                    tokenManager.clearToken()
                                    // Останавливаем воспроизведение при логауте
                                    val intent = Intent(this@MainActivity, PlaybackService::class.java)
                                    stopService(intent)

                                    // ✅ Сброс состояния ViewModel
                                    catalogViewModel.resetState()
                                    playlistsViewModel.resetState()
                                    coroutineScope.launch {
                                        tracksRepository.clearCache()
                                    }
                                    isAuthenticated = false
                                    rootNavController.navigate("auth") {
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
                                onThemeToggle = onThemeToggle,
                                catalogViewModel = catalogViewModel,
                                playlistsViewModel = playlistsViewModel
                            )
                        }
                    }

                    composable(
                        route = "playlist/{playlistId}",
                        arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: return@composable

                        PlaylistDetailScreen(
                            playlistId = playlistId,
                            viewModel = playlistsViewModel,
                            onTrackClick = { track ->
                                currentTrack = track
                                // Ищем треки в актуальном стейте Success
                                val state = playlistsViewModel.state.value
                                val playlist = if (state is PlaylistsState.Success) {
                                    state.playlists.find { it.id == playlistId }
                                } else null
                                
                                playlistTracks = playlist?.tracks ?: emptyList()
                                rootNavController.navigate("player/${track.id}")
                            },
                            onBack = { rootNavController.popBackStack() }
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
                                        rootNavController.popBackStack()
                                        rootNavController.navigate("player/${currentTrack?.id}")
                                    }
                                },
                                onPrevious = {
                                    val currentIndex = playlistTracks.indexOfFirst { it.id == track.id }
                                    if (currentIndex - 1 >= 0) {
                                        currentTrack = playlistTracks[currentIndex - 1]
                                        rootNavController.popBackStack()
                                        rootNavController.navigate("player/${currentTrack?.id}")
                                    }
                                },
                                onBack = {
                                    rootNavController.popBackStack()
                                }
                            )
                        } else {
                            rootNavController.popBackStack()
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