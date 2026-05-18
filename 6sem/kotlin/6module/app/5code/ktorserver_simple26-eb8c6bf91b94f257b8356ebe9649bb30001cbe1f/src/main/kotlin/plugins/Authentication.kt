package org.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.example.database.UserRepository

fun Application.configureAuthentication() {
    val jwtSecret = "my-super-secret-key-that-is-at-least-32-chars-long-123456"
    val jwtAudience = "nobel-prize-api"
    val userRepository = UserRepository()

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                val login = credential.payload.subject
                if (userRepository.userExists(login)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}