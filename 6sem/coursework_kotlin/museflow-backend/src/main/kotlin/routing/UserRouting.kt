package org.example.features.user

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.database.users.Users
import org.example.features.playlists.getUserIdFromToken

fun Application.configureUserRouting() {
    routing {
        authenticate("auth-jwt") {
            get("/user/me") {
                val userId = getUserIdFromToken(call)
                val user = Users.getById(userId)
                if (user != null) {
                    call.respond(mapOf(
                        "id" to user.id,
                        "login" to user.login,
                        "email" to user.email
                    ))
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }
        }
    }
}