package org.example.routing

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.example.database.UserFavorites
import org.example.database.Users
import org.example.database.Prizes
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class FavoritePrizeResponse(
    val id: String,
    val year: Int,
    val category: String
)

fun Application.favoritesRoutes() {
    routing {
        authenticate("auth-jwt") {
            // Получить избранные премии пользователя
            get("/users/me/prizes") {
                val principal = call.principal<JWTPrincipal>()
                val login = principal?.payload?.getClaim("sub")?.asString()

                if (login == null) {
                    call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Invalid token")
                    return@get
                }

                val userId = transaction {
                    Users.select { Users.login eq login }.single()[Users.id]
                }

                val favorites = transaction {
                    (UserFavorites innerJoin Prizes)
                        .select { UserFavorites.userId eq userId }
                        .map { row ->
                            FavoritePrizeResponse(
                                id = row[Prizes.id],
                                year = row[Prizes.awardYear],
                                category = row[Prizes.category]
                            )
                        }
                }
                call.respond(favorites)


                // Добавить в избранное
                post("/users/me/prizes/{prizeId}") {
                    val prizeId = call.parameters["prizeId"]
                    if (prizeId == null) {
                        call.respond(io.ktor.http.HttpStatusCode.BadRequest, "Prize ID required")
                        return@post
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val login = principal?.payload?.getClaim("sub")?.asString()

                    if (login == null) {
                        call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Invalid token")
                        return@post
                    }

                    transaction {
                        val userId = Users.select { Users.login eq login }.single()[Users.id]
                        UserFavorites.insert {
                            it[UserFavorites.userId] = userId
                            it[UserFavorites.prizeId] = prizeId
                            it[UserFavorites.addedAt] = System.currentTimeMillis()
                        }
                    }
                    call.respond(mapOf("message" to "Prize added to favorites"))
                }

                // Удалить из избранного
                delete("/users/me/prizes/{prizeId}") {
                    val prizeId = call.parameters["prizeId"]
                    if (prizeId == null) {
                        call.respond(io.ktor.http.HttpStatusCode.BadRequest, "Prize ID required")
                        return@delete
                    }

                    val principal = call.principal<JWTPrincipal>()
                    val login = principal?.payload?.getClaim("sub")?.asString()

                    if (login == null) {
                        call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Invalid token")
                        return@delete
                    }

                    transaction {
                        val userId = Users.select { Users.login eq login }.single()[Users.id]
                        UserFavorites.deleteWhere {
                            (UserFavorites.userId eq userId) and (UserFavorites.prizeId eq prizeId)
                        }
                    }
                    call.respond(mapOf("message" to "Prize removed from favorites"))
                }

                // Профиль пользователя
                get("/users/me") {
                    val principal = call.principal<JWTPrincipal>()
                    val login = principal?.payload?.getClaim("sub")?.asString()

                    println("Login from token: $login")

                    if (login == null) {
                        call.respond(io.ktor.http.HttpStatusCode.Unauthorized, "Invalid token")
                        return@get
                    }

                    val user = transaction {
                        Users.select { Users.login eq login }.single()
                    }
                    call.respond(
                        mapOf(
                            "login" to user[Users.login],
                            "role" to user[Users.role]
                        )
                    )
                }
            }
        }
    }
}

