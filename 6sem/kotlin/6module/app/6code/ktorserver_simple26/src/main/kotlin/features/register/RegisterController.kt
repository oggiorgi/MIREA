package org.example.features.register

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.example.database.UserRepository
import org.example.utils.isValidateEmail

class RegisterController(val call: ApplicationCall) {

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

        call.respond(RegisterResponseRemote(information = "Пользователь создался"))
    }
}