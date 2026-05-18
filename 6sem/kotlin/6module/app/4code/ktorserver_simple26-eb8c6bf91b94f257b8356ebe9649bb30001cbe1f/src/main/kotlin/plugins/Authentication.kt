package org.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.example.database.users.UserModel

fun Application.configureAuthentication() {
    val jwtSecret = "my-super-secret-key-that-is-at-least-32-chars-long-123456"
    val jwtAudience = "nobel-prize-api"

    install(Authentication) {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                val jwtPrincipal = JWTPrincipal(credential.payload)
                // Можно добавить дополнительную проверку пользователя в БД
                val login = jwtPrincipal.payload.subject
                if (UserModel.fetchUser(login) != null) {
                    jwtPrincipal
                } else {
                    null
                }
            }
        }
    }
}