package com.example.museflow.presentation.ui.genre

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Track
import com.example.museflow.presentation.ui.catalog.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreTracksScreen(
    genreName: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = genreName,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• ${tracks.size} треков",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Нет треков в жанре «$genreName»",
                        style = MaterialTheme.typography.bodyLarge,
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
                items(tracks, key = { it.id }) { track ->
                    GenreTrackItem(
                        track = track,
                        onClick = { onTrackClick(track) }
                    )
                }
            }
        }
    }
}

@Composable
fun GenreTrackItem(
    track: Track,
    onClick: () -> Unit
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
            // Обложка
            Image(
                painter = rememberAsyncImagePainter(
                    model = track.coverUrl,
                    error = androidx.compose.ui.res.painterResource(
                        id = android.R.drawable.ic_menu_gallery
                    )
                ),
                contentDescription = "Cover",
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Информация о треке
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = track.artist,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDuration(track.duration),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    if (track.genre != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = track.genre!!,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Иконка "Воспроизвести"
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, name = "Genre Tracks Screen Preview")
@Composable
fun GenreTracksScreenPreview() {
    MaterialTheme {
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
                title = "We Will Rock You",
                artist = "Queen",
                duration = 120,
                coverUrl = "https://picsum.photos/id/100/200",
                audioUrl = "",
                genre = "Rock"
            ),
            Track(
                id = 3,
                title = "Stairway to Heaven",
                artist = "Led Zeppelin",
                duration = 482,
                coverUrl = "https://picsum.photos/id/100/200",
                audioUrl = "",
                genre = "Rock"
            ),
            Track(
                id = 4,
                title = "Back in Black",
                artist = "AC/DC",
                duration = 254,
                coverUrl = "https://picsum.photos/id/100/200",
                audioUrl = "",
                genre = "Rock"
            )
        )

        GenreTracksScreen(
            genreName = "Rock",
            tracks = sampleTracks,
            onTrackClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty Genre Screen Preview")
@Composable
fun EmptyGenreScreenPreview() {
    MaterialTheme {
        GenreTracksScreen(
            genreName = "Jazz",
            tracks = emptyList(),
            onTrackClick = {},
            onBack = {}
        )
    }
}