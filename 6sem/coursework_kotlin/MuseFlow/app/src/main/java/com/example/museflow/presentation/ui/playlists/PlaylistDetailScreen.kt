package com.example.museflow.presentation.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.models.Track
import com.example.museflow.presentation.ui.catalog.formatDuration
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    viewModel: PlaylistsViewModel,
    onTrackClick: (Track) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    // 1. Храним "последний известный хороший" плейлист, чтобы экран не закрывался при Loading
    var lastKnownPlaylist by remember { mutableStateOf<Playlist?>(null) }

    // 2. Ищем плейлист в текущем стейте (только если Success)
    val currentPlaylistFromState = remember(state) {
        if (state is PlaylistsState.Success) {
            (state as PlaylistsState.Success).playlists.find { it.id == playlistId }
        } else null
    }

    // 3. Обновляем локальную копию только когда данные действительно пришли
    LaunchedEffect(currentPlaylistFromState) {
        if (currentPlaylistFromState != null) {
            lastKnownPlaylist = currentPlaylistFromState
        }
    }

    // 4. Логика выхода (только если плейлист реально удален из базы)
    LaunchedEffect(currentPlaylistFromState, state) {
        // Если мы в Success, а плейлиста нет – значит он удален
        if (state is PlaylistsState.Success && currentPlaylistFromState == null) {
            onBack()
        }
    }

    // Используем lastKnownPlaylist для отрисовки, это обеспечит стабильность UI
    val displayPlaylist = lastKnownPlaylist

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = displayPlaylist?.name ?: "Плейлист",
                            fontWeight = FontWeight.Bold
                        )
                        displayPlaylist?.let {
                            Text(
                                text = "${it.tracks.size} ${getTracksText(it.tracks.size)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        // Показываем индикатор только если данных ЕЩЕ НЕТ СОВСЕМ
        if (displayPlaylist == null && state is PlaylistsState.Loading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } 
        // Если данных нет и загрузка кончилась (плейлист удален)
        else if (displayPlaylist == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Плейлист удален", color = MaterialTheme.colorScheme.error)
            }
        }
        // Если плейлист найден, но треков в нем нет
        else if (displayPlaylist.tracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PlaylistPlay,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "В плейлисте пока нет треков",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } 
        // Отображаем список треков
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(displayPlaylist.tracks, key = { it.id }) { track ->
                    PlaylistTrackItem(
                        track = track,
                        onClick = { onTrackClick(track) },
                        onRemove = {
                            coroutineScope.launch {
                                viewModel.removeTrackFromPlaylist(playlistId, track.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistTrackItem(
    track: Track,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = track.coverUrl),
                contentDescription = "Cover",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = track.artist,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDuration(track.duration),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

fun getTracksText(count: Int): String {
    return when {
        count % 10 == 1 && count % 100 != 11 -> "трек"
        count % 10 in 2..4 && (count % 100 < 10 || count % 100 > 20) -> "трека"
        else -> "треков"
    }
}
