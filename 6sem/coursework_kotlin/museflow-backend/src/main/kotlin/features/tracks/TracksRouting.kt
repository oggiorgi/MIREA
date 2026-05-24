package org.example.features.tracks

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.database.tracks.Tracks

fun Application.configureTracksRouting() {
    routing {
        authenticate ("auth-jwt") {  // Защищённые эндпоинты (требуют токен)
            get("/tracks") {
                val tracks = Tracks.getAll()
                call.respond(tracks)
            }

            get("/tracks/search") {
                val query = call.request.queryParameters["q"] ?: ""
                val results = if (query.isNotBlank()) {
                    Tracks.search(query)
                } else {
                    emptyList()
                }
                call.respond(results)
            }

            get("/tracks/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid track ID")
                    return@get
                }
                val track = Tracks.getById(id)
                if (track == null) {
                    call.respond(HttpStatusCode.NotFound, "Track not found")
                } else {
                    call.respond(track)
                }
            }
        }
    }
}