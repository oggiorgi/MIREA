package com.example.museflow.presentation.ui.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.models.Track

@Composable
fun CatalogScreen(
    onTrackClick: (Track) -> Unit,
    onGenreClick: (String) -> Unit,
    playlists: List<Playlist> = emptyList(),
    onAddToPlaylist: (Int, Int) -> Unit,
    viewModel: CatalogViewModel
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTrackId by remember { mutableStateOf<Int?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedTrackId) {
        if (selectedTrackId != null) {
            showAddDialog = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.search(it)
            },
            label = { Text("Поиск") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        when (state) {
            is CatalogState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CatalogState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((state as CatalogState.Error).message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadTracks() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is CatalogState.Success -> {
                val tracks = (state as CatalogState.Success).tracks

                val tracksByGenre = tracks.groupBy { it.genre ?: "Другое" }
                val dailyPlaylistTracks = tracks.shuffled().take(3)

                LazyColumn {
                    if (dailyPlaylistTracks.isNotEmpty()) {
                        item {
                            DailyPlaylistSection(
                                tracks = dailyPlaylistTracks,
                                onTrackClick = onTrackClick,
                                onAddToPlaylist = { trackId ->
                                    selectedTrackId = trackId
                                }
                            )
                        }
                    }

                    tracksByGenre.forEach { (genre, genreTracks) ->
                        item {
                            GenreFolderSection(
                                genreName = genre,
                                tracks = genreTracks.take(5),
                                onTrackClick = onTrackClick,
                                onGenreClick = { onGenreClick(genre) },
                                onAddToPlaylist = { trackId ->
                                    selectedTrackId = trackId
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && selectedTrackId != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = {
                showAddDialog = false
                selectedTrackId = null
            },
            onAdd = { playlistId ->
                onAddToPlaylist(playlistId, selectedTrackId!!)
                showAddDialog = false
                selectedTrackId = null
            }
        )
    }
}

// ==================== НОВАЯ СЕКЦИЯ ТРЕКОВ ДНЯ ================================================================================
@Composable
fun DailyPlaylistSection(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onAddToPlaylist: (Int) -> Unit
) {
    // Берём 3 случайных трека
    val dailyTracks = tracks.shuffled().take(10)

    if (dailyTracks.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "🎧 Треки дня",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )


        // Вертикальный список из 3 карточек на весь экран
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            dailyTracks.forEach { track ->
                DailyPlaylistFullWidthCard(
                    track = track,
                    onTrackClick = onTrackClick,
                    onAddToPlaylist = onAddToPlaylist
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun DailyPlaylistFullWidthCard(
    track: Track,
    onTrackClick: (Track) -> Unit,
    onAddToPlaylist: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onTrackClick(track) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Обложка
            Image(
                painter = rememberAsyncImagePainter(model = track.coverUrl),
                contentDescription = "Cover",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Кнопка добавления
            IconButton(
                onClick = { onAddToPlaylist(track.id) }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to playlist"
                )
            }
        }
    }
}
// ==================== ОСТАЛЬНЫЕ КОМПОНЕНТЫ ====================================================================================================

@Composable
fun GenreFolderSection(
    genreName: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onGenreClick: () -> Unit,
    onAddToPlaylist: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onGenreClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = genreName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = " (${tracks.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(tracks.take(10)) { track ->
                GenreTrackCard(
                    track = track,
                    onClick = { onTrackClick(track) },
                    onAddToPlaylist = { onAddToPlaylist(track.id) }
                )
            }

            item {
                SeeAllButton(onClick = onGenreClick)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun GenreTrackCard(
    track: Track,
    onClick: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = track.coverUrl),
                contentDescription = "Cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = track.title,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Text(
                text = track.artist,
                fontSize = 10.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            IconButton(
                onClick = onAddToPlaylist,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add to playlist",
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Folder,
                contentDescription = "See all",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Все",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "→",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}