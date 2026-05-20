package org.example.routing

import io.ktor.server.application.Application
import io.ktor.server.response.*
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

// Класс определён ДО использования
@Serializable
data class Test(
    val text: String
)

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("hello, world")
        }
    }
}