package org.example.features.login

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureLoginRouting() {
    routing {
        post("/auth/login") {
            val loginController = LoginController(call)
            loginController.performLogin()
        }
    }
}