package org.example.features.login

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.database.users.Users
import org.example.utils.PasswordHasher
import java.util.*

class LoginController(private val call: ApplicationCall) {
    suspend fun performLogin() {
        val receive = call.receive<LoginReceiveRemote>()
        val userDTO = Users.fetchUser(receive.login)

        if (userDTO == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
        } else {
            if (PasswordHasher.verify(receive.password, userDTO.passwordHash)) {
                val token = generateJWT(userDTO.id, userDTO.login)
                call.respond(LoginResponseRemote(token = token))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid password")
            }
        }
    }

    private fun generateJWT(userId: Int, login: String): String {
        val secret = System.getenv("JWT_SECRET") ?: "MuseFlowSecretKey2025"
        return JWT.create()
            .withIssuer("MuseFlowServer")
            .withClaim("userId", userId)
            .withClaim("login", login)
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000))
            .sign(Algorithm.HMAC256(secret))
    }
}