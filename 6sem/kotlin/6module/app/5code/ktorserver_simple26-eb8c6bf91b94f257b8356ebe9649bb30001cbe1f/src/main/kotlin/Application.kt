package org.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.example.database.DatabaseFactory
import org.example.features.login.configureLoginRouting
import org.example.features.register.configureRegisterRouting
import org.example.plugins.configureAuthentication
import org.example.plugins.configureLogging
import org.example.plugins.configureOpenAPI
import org.example.routing.configureRouting
import org.example.routing.configureSerialization
import org.example.routing.favoritesRoutes
import org.example.routing.prizeRoutes
import org.example.services.NobelApiService

suspend fun main() {
    // Подключение к neon.tech:
    DatabaseFactory.init()
    // Загрузить данные из API при запуске
    val apiService = NobelApiService()
    apiService.fetchAndStorePrizes()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0")
    {
        configureSerialization()      // JSON сериализация
        configureAuthentication()     // JWT (НОВЫЙ)
        configureRouting()            // GET /
        configureLoginRouting()       // POST /login (ваш существующий)
        configureRegisterRouting()    // POST /register (ваш существующий)
        prizeRoutes()                 // GET /prizes и другие (НОВЫЙ)
        configureLogging()
        favoritesRoutes()
        configureOpenAPI()


        log.info("Nobel Prize API started on port 8080")
    }.start(wait = true)
}
