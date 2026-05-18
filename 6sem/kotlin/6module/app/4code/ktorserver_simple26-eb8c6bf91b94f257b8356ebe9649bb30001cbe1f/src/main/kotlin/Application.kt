package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.example.features.login.configureLoginRouting
import org.example.features.register.configureRegisterRouting
import org.example.plugins.configureAuthentication
import org.example.routing.configureRouting
import org.example.routing.configureSerialization
import org.example.routing.prizeRoutes
import org.jetbrains.exposed.sql.Database

fun main() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/museflow",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "1234"
    )

    embeddedServer(Netty, port = 8080, host = "0.0.0.0")
    {
        configureSerialization()      // JSON сериализация
        configureAuthentication()     // JWT (НОВЫЙ)
        configureRouting()            // GET /
        configureLoginRouting()       // POST /login (ваш существующий)
        configureRegisterRouting()    // POST /register (ваш существующий)
        prizeRoutes()                 // GET /prizes и другие (НОВЫ
        log.info("Nobel Prize API started on port 8080")
    }.start(wait = true)
}
