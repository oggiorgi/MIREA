package com.example.museflow.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
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
    private var serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val CHANNEL_ID = "playback_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PLAY = "play"
        const val ACTION_PAUSE = "pause"
        const val ACTION_NEXT = "next"
        const val ACTION_PREVIOUS = "previous"
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateNotification()
                }

                override fun onPositionDiscontinuity(reason: Int) {
                    updateNotification()
                }
            })
        }

        mediaSession = MediaSession.Builder(this, player!!).build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> player?.play()
            ACTION_PAUSE -> player?.pause()
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
                if (tracksList != null) {
                    startPlayback(tracksList, startIndex)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun startPlayback(tracks: List<Track>, startIndex: Int = 0) {
        currentTracks = tracks
        currentTrackIndex = startIndex
        playTrackAtIndex(startIndex)
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun playTrackAtIndex(index: Int) {
        if (index < 0 || index >= currentTracks.size) return

        currentTrackIndex = index

        val mediaItems = currentTracks.map { trackItem ->
            MediaItem.Builder()
                .setUri(trackItem.audioUrl)
                .setMediaId(trackItem.id.toString())
                .build()
        }

        player?.setMediaItems(mediaItems, index, 0)
        player?.prepare()
        player?.play()
        updateNotification()
    }

    private fun createNotification(): Notification {
        val currentTrack = if (currentTrackIndex in currentTracks.indices) {
            currentTracks[currentTrackIndex]
        } else null

        val playIntent = Intent(this, PlaybackService::class.java).apply {
            action = if (player?.isPlaying == true) ACTION_PAUSE else ACTION_PLAY
        }
        val playPendingIntent = PendingIntent.getService(
            this, 0, playIntent,
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
                if (player?.isPlaying == true) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (player?.isPlaying == true) "Pause" else "Play",
                playPendingIntent
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

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onDestroy() {
        serviceScope.cancel()
        player?.release()
        mediaSession?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = super.onBind(intent)
}