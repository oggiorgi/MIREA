package org.example.features.register

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.database.UserRepository
import org.example.utils.isValidateEmail
import java.util.*

class RegisterController(val call: ApplicationCall) {

    // JWT конфигурация
    private val jwtSecret = "my-super-secret-key-that-is-at-least-32-chars-long-123456"
    private val jwtIssuer = "nobel-prize-api"
    private val jwtAudience = "nobel-prize-api"
    private val jwtExpiryMinutes = 30

    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()
        val userRepository = UserRepository()

        // 1. Валидация email
        if (!registerReceiveRemote.email.isValidateEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            return
        }

        // 2. Проверка, существует ли пользователь
        if (userRepository.userExists(registerReceiveRemote.login)) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        }

        // 3. Создание пользователя (пока без хэширования пароля, потом добавим BCrypt)
        try {
            userRepository.createUser(
                login = registerReceiveRemote.login,
                passwordHash = registerReceiveRemote.password,  // TODO: добавить BCrypt
                role = "user"
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to create user")
            return
        }

        // 4. Генерируем JWT токен для нового пользователя
        val token = JWT.create()
            .withSubject(registerReceiveRemote.login)
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiryMinutes * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(RegisterResponseRemote(token = token))
    }
}