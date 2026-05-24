package org.example.database.tracks

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Tracks : Table("tracks") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    val artist = varchar("artist", 100)
    val duration = integer("duration") // секунды
    val coverUrl = varchar("cover_url", 500)
    val audioUrl = varchar("audio_url", 500)
    val genre = varchar("genre", 50).nullable()

    override val primaryKey = PrimaryKey(id)

    // Получить все треки
    fun getAll(): List<TrackDTO> = transaction {
        Tracks.selectAll().map { it.toDTO() }
    }

    // Получить трек по ID
    fun getById(trackId: Int): TrackDTO? = transaction {
        Tracks.select { Tracks.id eq trackId }
            .singleOrNull()
            ?.toDTO()
    }

    // Поиск треков
    fun search(query: String): List<TrackDTO> = transaction {
        Tracks.select {
            (Tracks.title like "%$query%") or (Tracks.artist like "%$query%")
        }.map { it.toDTO() }
    }

    private fun ResultRow.toDTO() = TrackDTO(
        id = this[Tracks.id],
        title = this[Tracks.title],
        artist = this[Tracks.artist],
        duration = this[Tracks.duration],
        coverUrl = this[Tracks.coverUrl],
        audioUrl = this[Tracks.audioUrl],
        genre = this[Tracks.genre]
    )
}