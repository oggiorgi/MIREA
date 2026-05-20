package org.example.database

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object SeedData {
    fun insertPrizes() {
        transaction {
            if (Prizes.selectAll().empty()) {
                val prizes = listOf(
                    Triple("physics_2023", 2023, "physics"),
                    Triple("chemistry_2023", 2023, "chemistry"),
                    Triple("medicine_2023", 2023, "medicine"),
                    Triple("physics_2022", 2022, "physics")
                )
                for ((id, year, category) in prizes) {
                    Prizes.insert {
                        it[Prizes.id] = id
                        it[Prizes.awardYear] = year
                        it[Prizes.category] = category
                        it[Prizes.categoryFullName] = category.replaceFirstChar { it.uppercase() }
                    }
                }
                println("Prizes seeded successfully")
            }
        }
    }
}