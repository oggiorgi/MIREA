package com.example.museflow.presentation.ui.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Track

@Composable
fun PlayerScreen(
    track: Track,
    playlistTracks: List<Track> = emptyList(),
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItems = if (playlistTracks.isNotEmpty()) {
                playlistTracks.map { trackItem ->
                    MediaItem.fromUri(Uri.parse(trackItem.audioUrl))
                }
            } else {
                listOf(MediaItem.fromUri(Uri.parse(track.audioUrl)))
            }
            val startIndex = playlistTracks.indexOfFirst { it.id == track.id }.coerceAtLeast(0)
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    PlayerScreenContent(
        track = track,
        isPlaying = exoPlayer.isPlaying,
        onPlayPause = {
            if (exoPlayer.isPlaying) exoPlayer.pause()
            else exoPlayer.play()
        },
        onNext = onNext,
        onPrevious = onPrevious,
        onBack = onBack,
        playerView = {
            AndroidView(
                factory = { PlayerView(context).apply { player = exoPlayer } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    )
}

@Composable
private fun PlayerScreenContent(
    track: Track,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onBack: () -> Unit,
    playerView: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(model = track.coverUrl),
                contentDescription = "Cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = track.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = track.artist,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        playerView()

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(onClick = onPlayPause) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Text("Назад")
        }
    }
}

// Preview для PlayerScreen(без ExoPlayer)
@Preview(showBackground = true, name = "Player Screen Preview")
@Composable
fun PlayerScreenPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlayerScreenContent(
                track = Track(
                    id = 1,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    duration = 354,
                    coverUrl = "https://picsum.photos/id/100/300",
                    audioUrl = "https://example.com/audio.mp3",
                    genre = "Rock"
                ),
                isPlaying = false,
                onPlayPause = {},
                onNext = {},
                onPrevious = {},
                onBack = {},
                playerView = {
                    // В preview показываем заглушку вместо ExoPlayer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🎵 Аудиоплеер",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}