package org.example.features.login

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.database.UserRepository
import java.util.*

class LoginController(private val call: ApplicationCall){

    private val jwtSecret = "my-super-secret-key-that-is-at-least-32-chars-long-123456"
    private val jwtIssuer = "nobel-prize-api"
    private val jwtAudience = "nobel-prize-api"
    private val jwtExpiryMinutes = 30

    suspend fun performLogin() {
        val receive = call.receive<LoginReceiveRemote>()
        val userRepository = UserRepository()

        // 1. Проверяем, существует ли пользователь
        val user = userRepository.findUserByLogin(receive.login)

        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
            return
        }

        // 2. Проверяем пароль (пока без хэширования, потом добавим BCrypt)
        if (receive.password != user.passwordHash) {
            call.respond(HttpStatusCode.BadRequest, "Invalid password")
            return
        }

        // 3. Генерируем JWT токен
        val token = JWT.create()
            .withSubject(receive.login)
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiryMinutes * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))

        call.respond(LoginResponseRemote(token = token))
    }
}