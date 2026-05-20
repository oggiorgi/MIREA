package org.example.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.features.prizes.NobelPrizeData
import kotlinx.serialization.Serializable

@Serializable
data class PrizeSummary(val year: Int, val category: String, val laureatesCount: Int)

@Serializable
data class PrizeDetail(val year: Int, val category: String, val laureates: List<LaureateResponse>)

@Serializable
data class LaureateResponse(val id: String, val name: String, val motivation: String, val share: Int)

// Изменяем с Routing на Application
fun Application.prizeRoutes() {  // ← Application, не Routing
    routing {  // ← Оборачиваем в routing {}
        authenticate("auth-jwt") {
            get("/prizes") {
                log.info("GET /prizes - fetching all prizes")
                val prizes = NobelPrizeData.getAllPrizes()
                val response = prizes.map { PrizeSummary(it.year, it.category, it.laureates.size) }
                log.info("GET /prizes - returned ${response.size} prizes")
                call.respond(response)
            }

            get("/prizes/{year}/{category}") {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]
                log.info("GET /prizes/$year/$category - fetching prize details")

                if (year != null && category != null) {
                    val prize = NobelPrizeData.getPrize(year, category)
                    if (prize != null) {
                        val response = PrizeDetail(
                            year = prize.year,
                            category = prize.category,
                            laureates = prize.laureates.map {
                                LaureateResponse(it.id, it.fullName, it.motivation, it.share)
                            }
                        )
                        call.respond(response)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Prize not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid year or category")
                }
            }

            get("/prizes/{year}/{category}/laureates") {
                val year = call.parameters["year"]?.toIntOrNull()
                val category = call.parameters["category"]

                if (year != null && category != null) {
                    val laureates = NobelPrizeData.getLaureates(year, category)
                    if (laureates != null) {
                        val response = laureates.map {
                            LaureateResponse(it.id, it.fullName, it.motivation, it.share)
                        }
                        call.respond(response)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Prize not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid year or category")
                }
            }
        }
    }
}