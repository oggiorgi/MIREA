package org.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureJWT() {
    val secret = System.getenv("JWT_SECRET") ?: "MuseFlowSecretKey2025"
    val issuer = "MuseFlowServer"

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val login = credential.payload.getClaim("login").asString()

                if (userId != null && login != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}