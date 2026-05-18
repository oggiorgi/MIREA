package org.example.features.register

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.database.users.UserDTO
import org.example.database.users.UserModel
import org.example.utils.isValidateEmail
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.util.*

class RegisterController(val call: ApplicationCall) {

    // JWT конфигурация
    private val jwtSecret = "my-super-secret-key-that-is-at-least-32-chars-long-123456"
    private val jwtIssuer = "nobel-prize-api"
    private val jwtAudience = "nobel-prize-api"
    private val jwtExpiryMinutes = 30

    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()

        if (!registerReceiveRemote.email.isValidateEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            return
        }

        val userDTO = UserModel.fetchUser(registerReceiveRemote.login)
        if (userDTO != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        }

        try {
            UserModel.insert(
                UserDTO(
                    login = registerReceiveRemote.login,
                    password = registerReceiveRemote.password,
                    email = registerReceiveRemote.email,
                    username = ""
                )
            )
        } catch (e: ExposedSQLException) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        }

        // Генерируем JWT токен для нового пользователя
        val token = JWT.create()
            .withSubject(registerReceiveRemote.login)
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiryMinutes * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))  // ← Правильный импорт и синтаксис

        call.respond(RegisterResponseRemote(token = token))
    }
}