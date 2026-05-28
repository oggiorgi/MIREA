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
            get("/test-faint") {
                val audioFile = File("uploads/audio/Linkin_Park_-_Faint.mp3")
                call.respondText(
                    "Faint - Linkin Park\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes\n" +
                            "Uploads dir exists: ${File("uploads").exists()}\n" +
                            "Uploads path: ${File("uploads").absolutePath}"
                )
            }

            get("/test-figure09") {
                val audioFile = File("uploads/audio/Linkin_Park_-_Figure09.mp3")
                call.respondText(
                    "Figure.09 - Linkin Park\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-fromtheinside") {
                val audioFile = File("uploads/audio/Linkin_Park_-_From_the_Inside.mp3")
                call.respondText(
                    "From the Inside - Linkin Park\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-lyingfromyou") {
                val audioFile = File("uploads/audio/Linkin_Park_-_Lying_from_You.mp3")
                call.respondText(
                    "Lying from You - Linkin Park\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-nobodyslistening") {
                val audioFile = File("uploads/audio/Linkin_Park_-_Nobodys_Listening.mp3")
                call.respondText(
                    "Nobodys Listening - Linkin Park\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-session") {
                val audioFile = File("uploads/audio/Linkin_Park_-_Session.mp3")
                call.respondText(
                    "Session - Linkin Park\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-control") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_Control.mp3")
                call.respondText(
                    "Control - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-die4guy") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_Die4Guy.mp3")
                call.respondText(
                    "Die4Guy - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-f33llik3dyin") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_F33l_Lik3_Dyin.mp3")
                call.respondText(
                    "F33l Lik3 Dyin - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-kingvamp") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_King_Vamp.mp3")
                call.respondText(
                    "King Vamp - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-nosl33p") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_No_Sl33p.mp3")
                call.respondText(
                    "No Sl33p - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-notplaying") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_Not_PLaying_.mp3")
                call.respondText(
                    "Not PLaying - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-over") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_Over.mp3")
                call.respondText(
                    "Over - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-slay3r") {
                val audioFile = File("uploads/audio/Playboi_Carti_-_Slay3r.mp3")
                call.respondText(
                    "Slay3r - Playboi Carti\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-go2damoon") {
                val audioFile = File("uploads/audio/Playboi_Carti_Kanye_West_-_Go2DaMoon.mp3")
                call.respondText(
                    "Go2DaMoon - Playboi Carti, Kanye West\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-nochnoekafe") {
                val audioFile = File("uploads/audio/Mirle_onda_andar_-_Nochnoe_kafe.mp3")
                call.respondText(
                    "Ночное кафе - Mirle onda andar\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
                )
            }

            get("/test-sharik") {
                val audioFile = File("uploads/audio/sharik_bytirka.mp3")
                call.respondText(
                    "Шарик - Бутырка\n" +
                            "Audio file exists: ${audioFile.exists()}\n" +
                            "Audio path: ${audioFile.absolutePath}\n" +
                            "File size: ${if (audioFile.exists()) audioFile.length() else 0} bytes"
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