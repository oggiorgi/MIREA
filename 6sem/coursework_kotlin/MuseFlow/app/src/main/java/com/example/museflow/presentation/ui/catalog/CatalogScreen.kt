package com.example.museflow.presentation.ui.catalog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.models.Track

@Composable
fun CatalogScreen(
    onTrackClick: (Track) -> Unit,
    playlists: List<Playlist> = emptyList(),
    onAddToPlaylist: (Int, Int) -> Unit,
    viewModel: CatalogViewModel = viewModel()
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
                if (tracks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ничего не найдено")
                    }
                } else {
                    LazyColumn {
                        items(tracks) { track ->
                            TrackItem(
                                track = track,
                                onClick = { onTrackClick(track) },
                                onAddToPlaylist = {
                                    selectedTrackId = track.id
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

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    onAddToPlaylist: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    fontSize = 16.sp
                )
                Text(
                    text = track.artist,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatDuration(track.duration),
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }

            onAddToPlaylist?.let {
                IconButton(onClick = it) {
                    Icon(Icons.Default.Add, contentDescription = "Add to playlist")
                }
            }
        }
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}

// Preview для TrackItem
@Preview(showBackground = true, name = "Track Item Preview")
@Composable
fun TrackItemPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            TrackItem(
                track = Track(
                    id = 1,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    duration = 354,
                    coverUrl = "https://picsum.photos/200",
                    audioUrl = "https://example.com/audio.mp3",
                    genre = "Rock"
                ),
                onClick = {},
                onAddToPlaylist = {}
            )
        }
    }
}

// Preview для CatalogScreen (с тестовыми данными)
@Preview(showBackground = true, name = "Catalog Screen Preview")
@Composable
fun CatalogScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Для preview используем заглушку без реального ViewModel
            val sampleTracks = listOf(
                Track(
                    id = 1,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    duration = 354,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                ),
                Track(
                    id = 2,
                    title = "Imagine",
                    artist = "John Lennon",
                    duration = 183,
                    coverUrl = "https://picsum.photos/id/101/200",
                    audioUrl = "",
                    genre = "Pop"
                ),
                Track(
                    id = 3,
                    title = "Billie Jean",
                    artist = "Michael Jackson",
                    duration = 294,
                    coverUrl = "https://picsum.photos/id/102/200",
                    audioUrl = "",
                    genre = "Pop"
                )
            )

            LazyColumn {
                items(sampleTracks) { track ->
                    TrackItem(
                        track = track,
                        onClick = {},
                        onAddToPlaylist = {}
                    )
                }
            }
        }
    }
}

// Preview для состояния загрузки (опционально)
@Preview(showBackground = true, name = "Loading State Preview")
@Composable
fun LoadingStatePreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}