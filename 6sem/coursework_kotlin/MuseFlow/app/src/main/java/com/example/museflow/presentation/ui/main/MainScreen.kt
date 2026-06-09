package com.example.museflow.presentation.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.museflow.data.network.auth.TokenManager
import com.example.museflow.domain.models.Track
import com.example.museflow.presentation.ui.catalog.CatalogScreen
import com.example.museflow.presentation.ui.catalog.CatalogViewModel
import com.example.museflow.presentation.ui.genre.GenreTracksScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsScreen
import com.example.museflow.presentation.ui.playlists.PlaylistsState
import com.example.museflow.presentation.ui.playlists.PlaylistsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * MainScreen — точка входа в основной функционал приложения после авторизации.
 * 
 * Данный экран реализует структуру с нижней навигацией (Bottom Navigation) и вложенным [NavHost].
 * Стратегия навигации основана на разделении ответственности между вкладками:
 * - Каталог: поиск и просмотр доступных треков.
 * - Плейлисты: управление личными коллекциями пользователя.
 * - Профиль: настройки аккаунта и приложения.
 * 
 * Переход на экран плеера осуществляется через внешний колбэк [onNavigateToPlayer],
 * что позволяет плееру оставаться активным поверх всех экранов навигации.
 */
@Composable
fun MainScreen(
    onNavigateToPlayer: (Track, List<Track>) -> Unit,
    onNavigateToPlaylist: (Int) -> Unit,
    coroutineScope: CoroutineScope,
    tokenManager: TokenManager,
    onLogout: () -> Unit,
    onClearCache: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    catalogViewModel: CatalogViewModel,
    playlistsViewModel: PlaylistsViewModel,
) {
    val bottomNavController = rememberNavController()
    val context = LocalContext.current

    // Синхронизация состояния плейлистов для отображения в различных разделах.
    // Это позволяет избежать повторных сетевых запросов при переключении между вкладками.
    val playlistsState by playlistsViewModel.state.collectAsState()
    val playlists = if (playlistsState is PlaylistsState.Success) {
        (playlistsState as PlaylistsState.Success).playlists
    } else {
        emptyList()
    }

    Scaffold(
        bottomBar = {
            // Реализация нижней панели навигации с динамической подсветкой активного маршрута
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    selected = currentRoute == "catalog",
                    onClick = { bottomNavController.navigate("catalog") },
                    label = { Text("Каталог") },
                    icon = { Icon(Icons.Default.MusicNote, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "playlists",
                    onClick = { bottomNavController.navigate("playlists") },
                    label = { Text("Плейлисты") },
                    icon = { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "profile",
                    onClick = { bottomNavController.navigate("profile") },
                    label = { Text("Профиль") },
                    icon = { Icon(Icons.Default.Person, null) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
            }
        }
    ) { paddingValues ->
        // Основной контейнер навигации авторизованной зоны.
        // Использование paddingValues обеспечивает корректный отступ контента от нижней панели.
        NavHost(
            navController = bottomNavController,
            startDestination = "catalog",
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            composable("catalog") {
                CatalogScreen(
                    onTrackClick = { track, allTracks ->
                        onNavigateToPlayer(track, allTracks)
                    },
                    onGenreClick = { genre ->
                        bottomNavController.navigate("genre/$genre")
                    },
                    playlists = playlists,
                    onAddToPlaylist = { playlistId, trackId ->
                        /*
                         * Логика добавления трека в плейлист.
                         * Запрос выполняется асинхронно через Ktor-клиент во ViewModel.
                         * Использование [coroutineScope] здесь оправдано необходимостью отображения 
                         * Toast в контексте UI после завершения сетевой операции.
                         */
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
                    viewModel = catalogViewModel,
                    tokenManager = tokenManager
                )
            }
            composable("playlists") {
                PlaylistsScreen(
                    onPlaylistClick = { playlist ->
                        // Переход обрабатывается в MainActivity, но здесь мы просто передаём наружу
                        onNavigateToPlaylist(playlist.id)
                    },
                    viewModel = playlistsViewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    tokenManager = tokenManager,
                    onLogout = onLogout,
                    onClearCache = onClearCache,
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
                        onNavigateToPlayer(track, tracks)
                    },
                    onBack = bottomNavController::popBackStack
                )
            }
        }
    }
}
