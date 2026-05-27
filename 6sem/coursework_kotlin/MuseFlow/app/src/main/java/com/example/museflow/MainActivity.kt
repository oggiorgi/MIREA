package com.example.museflow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.domain.repository.TracksRepository
import com.example.museflow.domain.models.Track
import com.example.museflow.presentation.ui.auth.AuthScreen
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
    lateinit var tracksRepository: TracksRepository  // ← внедряем через Hilt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MuseFlowTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

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
                        AuthScreen(
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
                            }
                        )
                    }
                    composable("player/{trackId}") {
                        val track = currentTrack
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