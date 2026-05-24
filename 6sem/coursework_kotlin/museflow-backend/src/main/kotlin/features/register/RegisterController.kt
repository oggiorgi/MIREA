package org.example.features.register

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.database.tokens.TokenDTO
import org.example.database.users.Users
import org.example.utils.PasswordHasher
import org.example.utils.isValidateEmail
import java.util.*

class RegisterController(val call: ApplicationCall) {

    suspend fun registerNewUser() {
        val request = call.receive<RegisterReceiveRemote>()

        if (!request.email.isValidateEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Invalid email")
            return
        }

        val existingUser = Users.fetchUser(request.login)
        if (existingUser != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        }

        val passwordHash = PasswordHasher.hash(request.password)
        val user = Users.create(request.login, request.email, passwordHash)

        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "Could not create user")
            return
        }

        // Генерируем JWT
        //val token = generateJWT(user.id, user.login)
        call.respond(RegisterResponseRemote(message = "User registered successfully"))                     //hello
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