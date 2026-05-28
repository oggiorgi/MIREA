package com.example.museflow.presentation.ui.player

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Track
import com.example.museflow.ui.theme.MuseFlowTheme

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

    // Оборачиваем в Surface для правильного фона
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background  // ← фон из темы
    ) {
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
        // Кнопка "Назад" в верхнем левом углу
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Обложка
        Card(
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = track.coverUrl),
                contentDescription = "Cover",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Название трека
        Text(
            text = track.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Исполнитель
        Text(
            text = track.artist,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Плеер
        playerView()

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки управления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onPrevious
            ) {
                Icon(
                    Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            IconButton(
                onClick = onNext
            ) {
                Icon(
                    Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ==================== PREVIEWS ====================

// Preview экрана плеера в светлой теме
@Preview(showBackground = true, name = "Player Screen - Light Theme")
@Composable
fun PlayerScreenLightPreview() {
    MuseFlowTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlayerScreenContent(
                track = Track(
                    id = 1,
                    title = "Imagine",
                    artist = "John Lennon",
                    duration = 183,
                    coverUrl = "https://picsum.photos/id/100/300",
                    audioUrl = "https://example.com/audio.mp3",
                    genre = "Pop"
                ),
                isPlaying = false,
                onPlayPause = {},
                onNext = {},
                onPrevious = {},
                onBack = {},
                playerView = {
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

// Preview экрана плеера в тёмной теме
@Preview(showBackground = true, name = "Player Screen - Dark Theme")
@Composable
fun PlayerScreenDarkPreview() {
    MuseFlowTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlayerScreenContent(
                track = Track(
                    id = 1,
                    title = "Imagine",
                    artist = "John Lennon",
                    duration = 183,
                    coverUrl = "https://picsum.photos/id/100/300",
                    audioUrl = "https://example.com/audio.mp3",
                    genre = "Pop"
                ),
                isPlaying = false,
                onPlayPause = {},
                onNext = {},
                onPrevious = {},
                onBack = {},
                playerView = {
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