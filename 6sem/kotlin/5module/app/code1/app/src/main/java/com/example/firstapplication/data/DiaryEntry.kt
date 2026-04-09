package com.example.firstapplication.data

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class DiaryEntry(
    val filename: String,
    val title: String,
    val content: String,
    val timestamp: Long
) {
    fun getPreview(): String {
        return if (content.length > 40) {
            content.substring(0, 40) + "..."
        } else {
            content
        }
    }
    
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    companion object {
        fun createFilename(title: String?, timestamp: Long): String {
            val safeTitle = title?.replace(Regex("[^a-zA-Z0-9]"), "_")?.take(30)
            return if (safeTitle.isNullOrEmpty()) {
                "${timestamp}.txt"
            } else {
                "${timestamp}_${safeTitle}.txt"
            }
        }
        
        fun parseFilename(filename: String): Pair<Long, String> {
            val parts = filename.removeSuffix(".txt").split("_", limit = 2)
            val timestamp = parts.firstOrNull()?.toLongOrNull() ?: System.currentTimeMillis()
            val title = parts.getOrNull(1) ?: ""
            return Pair(timestamp, title)
        }
        
        fun fromFile(file: File): DiaryEntry {
            val (timestamp, title) = parseFilename(file.name)
            val content = file.readText()
            return DiaryEntry(
                filename = file.name,
                title = title,
                content = content,
                timestamp = timestamp
            )
        }
    }
}