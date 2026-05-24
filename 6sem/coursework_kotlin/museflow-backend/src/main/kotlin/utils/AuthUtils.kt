package org.example.utils

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

suspend fun getUserIdFromToken(call: ApplicationCall): Int {
    val principal = call.principal<JWTPrincipal>()
    return principal?.payload?.getClaim("userId")?.asInt()
        ?: throw Exception("User not authenticated")
}