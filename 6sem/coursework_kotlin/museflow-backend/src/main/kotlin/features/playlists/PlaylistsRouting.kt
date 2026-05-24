package org.example.features.playlists

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.database.playlists.PlaylistDTO
import org.example.database.playlists.PlaylistTracks
import org.example.database.playlists.Playlists
import kotlinx.serialization.Serializable
import org.example.utils.getUserIdFromToken

fun Application.configurePlaylistsRouting() {
    routing {
        authenticate("auth-jwt") {
            get("/playlists") {
                val userId = getUserIdFromToken(call)
                val playlists = Playlists.getUserPlaylists(userId)
                call.respond(playlists)
            }

            post("/playlists") {
                val userId = getUserIdFromToken(call)
                val request = call.receive<CreatePlaylistRequest>()
                val playlist = Playlists.create(userId, request.name, request.coverUrl)
                if (playlist != null) {
                    call.respond(HttpStatusCode.Created, playlist)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create playlist")
                }
            }

            put("/playlists/{id}") {
                val playlistId = call.parameters["id"]?.toIntOrNull()
                if (playlistId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid playlist ID")
                    return@put
                }
                val request = call.receive<UpdatePlaylistRequest>()
                val updated = Playlists.updateName(playlistId, request.name)
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Playlist updated")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Playlist not found")
                }
            }

            delete("/playlists/{id}") {
                val playlistId = call.parameters["id"]?.toIntOrNull()
                if (playlistId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid playlist ID")
                    return@delete
                }
                val deleted = Playlists.delete(playlistId)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Playlist deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Playlist not found")
                }
            }

            post("/playlists/{id}/tracks") {
                val playlistId = call.parameters["id"]?.toIntOrNull()
                if (playlistId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid playlist ID")
                    return@post
                }
                val request = call.receive<AddTrackRequest>()
                val added = PlaylistTracks.addTrack(playlistId, request.trackId)
                if (added) {
                    call.respond(HttpStatusCode.OK, "Track added")
                } else {
                    call.respond(HttpStatusCode.Conflict, "Track already in playlist")
                }
            }

            delete("/playlists/{id}/tracks/{trackId}") {
                val playlistId = call.parameters["id"]?.toIntOrNull()
                val trackId = call.parameters["trackId"]?.toIntOrNull()
                if (playlistId == null || trackId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid IDs")
                    return@delete
                }
                val removed = PlaylistTracks.removeTrack(playlistId, trackId)
                if (removed) {
                    call.respond(HttpStatusCode.OK, "Track removed")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Track not found in playlist")
                }
            }
        }
    }
}


@Serializable
data class CreatePlaylistRequest(val name: String, val coverUrl: String? = null)

@Serializable
data class UpdatePlaylistRequest(val name: String)

@Serializable
data class AddTrackRequest(val trackId: Int)