package com.example.museflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.data.network.client.RetrofitClient
import com.example.museflow.data.repository.AuthRepositoryImpl
import com.example.museflow.data.repository.PlaylistsRepositoryImpl
import com.example.museflow.data.repository.TracksRepositoryImpl
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.usecase.AddTrackToPlaylistUseCase
import com.example.museflow.domain.usecase.CreatePlaylistUseCase
import com.example.museflow.domain.usecase.DeletePlaylistUseCase
import com.example.museflow.domain.usecase.GetPlaylistsUseCase
import com.example.museflow.domain.usecase.GetTracksUseCase
import com.example.museflow.domain.usecase.LoginUseCase
import com.example.museflow.domain.usecase.RegisterUseCase
import com.example.museflow.domain.usecase.RemoveTrackFromPlaylistUseCase
import com.example.museflow.domain.usecase.SearchTracksUseCase
import com.example.museflow.presentation.ui.auth.AuthScreen
import com.example.museflow.presentation.ui.auth.AuthViewModel
import com.example.museflow.presentation.ui.auth.AuthViewModelFactory
import com.example.museflow.presentation.ui.main.MainScreen
import com.example.museflow.presentation.ui.player.PlayerScreen
import com.example.museflow.ui.theme.MuseFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.provideApiService(tokenManager)

        val authRepository = AuthRepositoryImpl(apiService, tokenManager)
        val tracksRepository = TracksRepositoryImpl(apiService)
        val playlistsRepository = PlaylistsRepositoryImpl(apiService)

        val loginUseCase = LoginUseCase(authRepository)
        val registerUseCase = RegisterUseCase(authRepository)
        val getTracksUseCase = GetTracksUseCase(tracksRepository)
        val searchTracksUseCase = SearchTracksUseCase(tracksRepository)
        val getPlaylistsUseCase = GetPlaylistsUseCase(playlistsRepository)
        val createPlaylistUseCase = CreatePlaylistUseCase(playlistsRepository)
        val deletePlaylistUseCase = DeletePlaylistUseCase(playlistsRepository)
        val addTrackToPlaylistUseCase = AddTrackToPlaylistUseCase(playlistsRepository)
        val removeTrackFromPlaylistUseCase = RemoveTrackFromPlaylistUseCase(playlistsRepository)

        setContent {
            MuseFlowTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(loginUseCase, registerUseCase)
                )

                var isAuthenticated by remember { mutableStateOf(tokenManager.getToken() != null) }
                var currentTrack by remember { mutableStateOf<Track?>(null) }
                var playlistTracks by remember { mutableStateOf<List<Track>>(emptyList()) }

                // Убираем Scaffold, так как NavHost сам управляет внутренними отступами
                NavHost(
                    navController = navController,
                    startDestination = if (isAuthenticated) "main" else "auth",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("auth") {
                        AuthScreen(authViewModel) { token ->
                            isAuthenticated = true
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    }
                    composable("main") {
                        MainScreen(
                            onNavigateToPlayer = { track, tracks ->
                                currentTrack = track
                                playlistTracks = tracks
                                navController.navigate("player/${track.id}")
                            },
                            getTracksUseCase = getTracksUseCase,
                            searchTracksUseCase = searchTracksUseCase,
                            getPlaylistsUseCase = getPlaylistsUseCase,
                            createPlaylistUseCase = createPlaylistUseCase,
                            deletePlaylistUseCase = deletePlaylistUseCase,
                            addTrackToPlaylistUseCase = addTrackToPlaylistUseCase,
                            coroutineScope = rememberCoroutineScope()
                        )
                    }
                    composable("player/{trackId}") { backStackEntry ->
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