package com.example.museflow.utils

import java.util.Locale

object FormatUtils {
    fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, secs)
    }

    fun getTracksText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && (count % 100 !in 10..20) -> "трека"
            else -> "треков"
        }
    }
}