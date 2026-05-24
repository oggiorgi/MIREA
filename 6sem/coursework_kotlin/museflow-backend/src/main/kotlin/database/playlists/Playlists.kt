package org.example.database.playlists

import org.example.database.tracks.TrackDTO
import org.example.database.tracks.Tracks
import org.example.database.users.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object Playlists : Table("playlists") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val name = varchar("name", 100)
    val coverUrl = varchar("cover_url", 500).nullable()
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        foreignKey(userId to Users.id)
    }

    fun create(userId: Int, name: String, coverUrl: String? = null): PlaylistDTO? = transaction {
        Playlists.insert {
            it[Playlists.userId] = userId
            it[Playlists.name] = name
            it[Playlists.coverUrl] = coverUrl
            it[Playlists.createdAt] = LocalDateTime.now()
        }

        val playlistId = Playlists.select {
            (Playlists.userId eq userId) and (Playlists.name eq name)
        }
            .orderBy(Playlists.id to SortOrder.DESC)
            .firstOrNull()
            ?.get(Playlists.id)

        playlistId?.let { getById(it) }
    }

    fun getById(playlistId: Int): PlaylistDTO? = transaction {
        Playlists.select { Playlists.id eq playlistId }
            .singleOrNull()
            ?.let { row ->
                PlaylistDTO(
                    id = row[Playlists.id],
                    userId = row[Playlists.userId],
                    name = row[Playlists.name],
                    coverUrl = row[Playlists.coverUrl],
                    createdAt = row[Playlists.createdAt].toString(),
                    tracks = getTracks(playlistId)
                )
            }
    }

    fun getUserPlaylists(userId: Int): List<PlaylistDTO> = transaction {
        Playlists.select { Playlists.userId eq userId }
            .map { row ->
                PlaylistDTO(
                    id = row[Playlists.id],
                    userId = row[Playlists.userId],
                    name = row[Playlists.name],
                    coverUrl = row[Playlists.coverUrl],
                    createdAt = row[Playlists.createdAt].toString(),
                    tracks = getTracks(row[Playlists.id])
                )
            }
    }

    fun delete(playlistId: Int): Boolean = transaction {
        PlaylistTracks.deleteWhere { PlaylistTracks.playlistId eq playlistId }
        Playlists.deleteWhere { Playlists.id eq playlistId } > 0
    }

    fun updateName(playlistId: Int, newName: String): Boolean = transaction {
        Playlists.update({ Playlists.id eq playlistId }) {
            it[Playlists.name] = newName
        } > 0
    }

    private fun getTracks(playlistId: Int): List<TrackDTO> = transaction {
        (PlaylistTracks innerJoin Tracks)
            .select { PlaylistTracks.playlistId eq playlistId }
            .orderBy(PlaylistTracks.orderIndex to SortOrder.ASC)
            .map { row ->
                TrackDTO(
                    id = row[Tracks.id],
                    title = row[Tracks.title],
                    artist = row[Tracks.artist],
                    duration = row[Tracks.duration],
                    coverUrl = row[Tracks.coverUrl],
                    audioUrl = row[Tracks.audioUrl],
                    genre = row[Tracks.genre]
                )
            }
    }
}

object PlaylistTracks : Table("playlist_tracks") {
    val playlistId = integer("playlist_id")
    val trackId = integer("track_id")
    val orderIndex = integer("order_index")

    override val primaryKey = PrimaryKey(playlistId, trackId)

    init {
        foreignKey(playlistId to Playlists.id)
        foreignKey(trackId to Tracks.id)
    }

    fun addTrack(playlistId: Int, trackId: Int): Boolean = transaction {
        val currentMaxOrder = PlaylistTracks
            .select { PlaylistTracks.playlistId eq playlistId }
            .count()

        PlaylistTracks.insert {
            it[PlaylistTracks.playlistId] = playlistId
            it[PlaylistTracks.trackId] = trackId
            it[orderIndex] = currentMaxOrder.toInt()
        }.insertedCount > 0
    }

    fun removeTrack(playlistId: Int, trackId: Int): Boolean = transaction {
        PlaylistTracks.deleteWhere {
            (PlaylistTracks.playlistId eq playlistId) and (PlaylistTracks.trackId eq trackId)
        } > 0
    }
}