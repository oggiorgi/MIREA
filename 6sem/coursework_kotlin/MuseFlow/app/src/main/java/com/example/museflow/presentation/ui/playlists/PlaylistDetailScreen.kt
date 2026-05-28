package com.example.museflow.presentation.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.museflow.presentation.ui.catalog.formatDuration
import com.example.museflow.ui.theme.MuseFlowTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlist: Playlist,
    onTrackClick: (Track) -> Unit,
    onRemoveTrack: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = playlist.name,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${playlist.tracks.size} треков",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
        if (playlist.tracks.isEmpty()) {
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Добавьте треки из каталога",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(playlist.tracks, key = { it.id }) { track ->
                    PlaylistTrackItem(
                        track = track,
                        onClick = { onTrackClick(track) },
                        onRemove = { onRemoveTrack(track.id) }
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

// ==================== PREVIEWS ====================


// Preview карточки трека в светлой теме
@Preview(showBackground = true, name = "Playlist Track Item - Light")
@Composable
fun PlaylistTrackItemLightPreview() {
    MuseFlowTheme(darkTheme = false) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            PlaylistTrackItem(
                track = Track(
                    id = 1,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    duration = 354,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                ),
                onClick = {},
                onRemove = {}
            )
        }
    }
}

// Preview карточки трека в тёмной теме (здесь должны быть тёмные карточки)
@Preview(showBackground = true, name = "Playlist Track Item - Dark")
@Composable
fun PlaylistTrackItemDarkPreview() {
    MuseFlowTheme(darkTheme = true) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            PlaylistTrackItem(
                track = Track(
                    id = 1,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    duration = 354,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                ),
                onClick = {},
                onRemove = {}
            )
        }
    }
}

// Preview списка треков в плейлисте (светлая тема)
@Preview(showBackground = true, name = "Playlist Detail Screen - Light")
@Composable
fun PlaylistDetailScreenLightPreview() {
    MuseFlowTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                    title = "Stairway to Heaven",
                    artist = "Led Zeppelin",
                    duration = 482,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                ),
                Track(
                    id = 3,
                    title = "Hey Jude",
                    artist = "The Beatles",
                    duration = 431,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                )
            )

            PlaylistDetailScreen(
                playlist = Playlist(
                    id = 1,
                    name = "Rock Classics",
                    coverUrl = null,
                    tracks = sampleTracks
                ),
                onTrackClick = {},
                onRemoveTrack = {},
                onBack = {}
            )
        }
    }
}

// Preview списка треков в плейлисте (тёмная тема)
@Preview(showBackground = true, name = "Playlist Detail Screen - Dark")
@Composable
fun PlaylistDetailScreenDarkPreview() {
    MuseFlowTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
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
                    title = "Stairway to Heaven",
                    artist = "Led Zeppelin",
                    duration = 482,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                ),
                Track(
                    id = 3,
                    title = "Hey Jude",
                    artist = "The Beatles",
                    duration = 431,
                    coverUrl = "https://picsum.photos/id/100/200",
                    audioUrl = "",
                    genre = "Rock"
                )
            )

            PlaylistDetailScreen(
                playlist = Playlist(
                    id = 1,
                    name = "Rock Classics",
                    coverUrl = null,
                    tracks = sampleTracks
                ),
                onTrackClick = {},
                onRemoveTrack = {},
                onBack = {}
            )
        }
    }
}