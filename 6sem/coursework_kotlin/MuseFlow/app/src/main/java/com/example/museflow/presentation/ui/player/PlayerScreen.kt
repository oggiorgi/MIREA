package com.example.museflow.presentation.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Track
import com.example.museflow.services.PlaybackService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(
    track: Track,
    playlistTracks: List<Track> = emptyList(),
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isPlaying by remember { mutableStateOf(false) }
    var currentTrackTitle by remember { mutableStateOf(track.title) }
    var currentTrackArtist by remember { mutableStateOf(track.artist) }
    var currentCoverUrl by remember { mutableStateOf(track.coverUrl) }
    var currentPosition by remember { mutableStateOf(0L) }
    var currentDuration by remember { mutableStateOf(track.duration * 1000L) }
    var isDurationFinal by remember { mutableStateOf(false) } // Флаг: получили ли мы точное время от плеера
    var serviceBound by remember { mutableStateOf(false) }
    var playbackService by remember { mutableStateOf<PlaybackService?>(null) }

    // Форматирование времени (миллисекунды -> MM:SS)
    fun formatTime(ms: Long): String {
        // Если время отрицательное или слишком большое (глюк плеера), возвращаем 0:00
        if (ms <= 0 || ms > 36000000) return "0:00"
        val seconds = ms / 1000
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, secs)
    }

    // Синхронизация при изменении входящего трека (например, при ручной навигации)
    LaunchedEffect(track) {
        currentTrackTitle = track.title
        currentTrackArtist = track.artist
        currentCoverUrl = track.coverUrl
        currentDuration = track.duration * 1000L
        isDurationFinal = false // Сбрасываем флаг для нового трека
    }

    // Подключение к сервису для получения состояния
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as PlaybackService.LocalBinder
                val s = binder.getService()
                playbackService = s
                serviceBound = true
                
                // Начинаем обновлять состояние
                scope.launch {
                    while (serviceBound) {
                        s.let {
                            isPlaying = it.isPlaying()
                            currentPosition = it.getCurrentPosition()
                            
                            // ✅ Обновляем длительность только если она пришла корректная
                            val duration = it.getDuration()
                            if (duration > 0) {
                                // Если разница с текущим временем больше 2 секунд или мы еще не зафиксировали время
                                if (!isDurationFinal || Math.abs(currentDuration - duration) > 2000) {
                                    currentDuration = duration
                                    isDurationFinal = true
                                }
                            }

                            val serviceTrack = it.getCurrentTrack()
                            if (serviceTrack != null) {
                                // Если сменился трек в сервисе (автопереход)
                                if (currentTrackTitle != serviceTrack.title) {
                                    currentTrackTitle = serviceTrack.title
                                    currentTrackArtist = serviceTrack.artist
                                    currentCoverUrl = serviceTrack.coverUrl
                                    // Обнуляем флаг фиксации для нового трека
                                    isDurationFinal = false
                                }
                                
                                // Также обновляем длительность из метаданных нового трека если плеер еще не выдал свою
                                if (!isDurationFinal) {
                                    currentDuration = serviceTrack.duration * 1000L
                                }
                            }
                        }
                        delay(500)
                    }
                }
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                serviceBound = false
                playbackService = null
            }
        }
    }

    // Запускаем сервис и подключаемся
    LaunchedEffect(Unit) {
        val intent = Intent(context, PlaybackService::class.java).apply {
            putExtra("tracks", ArrayList(playlistTracks))
            putExtra("startIndex", playlistTracks.indexOfFirst { it.id == track.id }.coerceAtLeast(0))
        }
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    DisposableEffect(Unit) {
        onDispose {
            context.unbindService(connection)
        }
    }

    // Отправка команд в сервис
    val sendCommand = { action: String ->
        Intent(context, PlaybackService::class.java).apply {
            this.action = action
        }.also { context.startService(it) }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.size(250.dp).padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentCoverUrl),
                    contentDescription = "Cover",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = currentTrackTitle, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentTrackArtist, style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(32.dp))

            // ==================== SEEK BAR ====================
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ползунок перемотки
                Slider(
                    value = if (currentDuration > 0) currentPosition.toFloat() / currentDuration.toFloat() else 0f,
                    onValueChange = { newValue ->
                        val newPosition = (newValue * currentDuration).toLong()
                        playbackService?.seekTo(newPosition)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Текущее время и длительность
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(currentDuration),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ==================== КНОПКИ УПРАВЛЕНИЯ ====================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    sendCommand(PlaybackService.ACTION_PREVIOUS)
                    onPrevious()
                }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(48.dp))
                }

                Spacer(modifier = Modifier.width(32.dp))

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(72.dp)
                ) {
                    IconButton(
                        onClick = { sendCommand(PlaybackService.ACTION_PLAY_PAUSE) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(32.dp))

                IconButton(onClick = {
                    sendCommand(PlaybackService.ACTION_NEXT)
                    onNext()
                }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}