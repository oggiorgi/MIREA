package com.example.museflow.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.museflow.MainActivity
import com.example.museflow.domain.models.Track
import kotlinx.coroutines.*

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var player: ExoPlayer? = null
    private var currentTracks: List<Track> = emptyList()
    private var currentTrackIndex: Int = 0
    private var currentIsPlaying: Boolean = false
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "playback_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY_PAUSE = "play_pause"
        const val ACTION_NEXT = "next"
        const val ACTION_PREVIOUS = "previous"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Создаём плеер на главном потоке
        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    currentIsPlaying = playbackState == Player.STATE_READY && isPlaying
                    updateNotification()
                }
                override fun onIsPlayingChanged(playing: Boolean) {
                    currentIsPlaying = playing
                    updateNotification()
                }
            })
        }
        mediaSession = MediaSession.Builder(this, player!!).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                player?.let {
                    if (it.isPlaying) it.pause() else it.play()
                }
            }
            ACTION_NEXT -> {
                val nextIndex = currentTrackIndex + 1
                if (nextIndex < currentTracks.size) {
                    playTrackAtIndex(nextIndex)
                }
            }
            ACTION_PREVIOUS -> {
                val prevIndex = currentTrackIndex - 1
                if (prevIndex >= 0) {
                    playTrackAtIndex(prevIndex)
                }
            }
            else -> {
                @Suppress("UNCHECKED_CAST")
                val tracksList = intent?.getSerializableExtra("tracks") as? ArrayList<Track>
                val startIndex = intent?.getIntExtra("startIndex", 0) ?: 0
                if (tracksList != null && tracksList.isNotEmpty()) {
                    startPlayback(tracksList, startIndex)
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startPlayback(tracks: List<Track>, startIndex: Int = 0) {
        currentTracks = tracks
        currentTrackIndex = startIndex
        playTrackAtIndex(startIndex)
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun playTrackAtIndex(index: Int) {
        if (index < 0 || index >= currentTracks.size) return

        currentTrackIndex = index
        val playerInstance = player ?: return

        val mediaItems = currentTracks.map { trackItem ->
            MediaItem.Builder()
                .setUri(trackItem.audioUrl)
                .setMediaId(trackItem.id.toString())
                .build()
        }

        playerInstance.setMediaItems(mediaItems, index, 0)
        playerInstance.prepare()
        playerInstance.play()
        updateNotification()
    }

    private fun createNotification(): Notification {
        val currentTrack = if (currentTrackIndex in currentTracks.indices) {
            currentTracks[currentTrackIndex]
        } else null

        val playPauseIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getService(
            this, 0, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getService(
            this, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent = Intent(this, PlaybackService::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val prevPendingIntent = PendingIntent.getService(
            this, 0, prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = Intent(this, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentTrack?.title ?: "MuseFlow")
            .setContentText(currentTrack?.artist ?: "Воспроизведение")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(openPendingIntent)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPendingIntent)
            .addAction(
                if (currentIsPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (currentIsPlaying) "Pause" else "Play",
                playPausePendingIntent
            )
            .addAction(android.R.drawable.ic_media_next, "Next", nextPendingIntent)
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Музыкальный плеер",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Управление воспроизведением музыки"
                setSound(null, null)
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    fun isPlaying(): Boolean = player?.isPlaying ?: false

    fun getCurrentTrack(): Track? = if (currentTrackIndex in currentTracks.indices) {
        currentTracks[currentTrackIndex]
    } else null

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return LocalBinder()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        serviceScope.cancel()
        player?.release()
        mediaSession?.release()
        super.onDestroy()
    }
}