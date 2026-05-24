package org.example

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.features.login.configureLoginRouting
import org.example.features.playlists.configurePlaylistsRouting
import org.example.features.register.configureRegisterRouting
import org.example.features.tracks.configureTracksRouting
import org.example.features.user.configureUserRouting
import org.example.routing.configureRouting
import org.example.routing.configureSerialization
import org.example.utils.configureJWT
import org.example.utils.configureLogging
import org.jetbrains.exposed.sql.Database
import java.io.File

fun main() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/museflow",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "1234"
    )

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.Authorization)
            allowHeader(HttpHeaders.ContentType)
            anyHost()
        }

        configureSerialization()
        configureJWT()

        routing {
            // Статические файлы
            staticFiles("/uploads/audio", File("uploads/audio"))
            staticFiles("/uploads/covers", File("uploads/covers"))

            // Для отладки
            get("/test-static") {
                val audioFile = File("uploads/audio/sharik_bytirka.mp3")
                call.respondText(
                    "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "Uploads dir exists: ${File("uploads").exists()}\n" +
                            "Uploads path: ${File("uploads").absolutePath}"
                )
            }
        }

        configureRouting()
        configureLoginRouting()
        configureRegisterRouting()
        configureTracksRouting()
        configurePlaylistsRouting()
        configureUserRouting()
        configureLogging()
    }.start(wait = true)
}