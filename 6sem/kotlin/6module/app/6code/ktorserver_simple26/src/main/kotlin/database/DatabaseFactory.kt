package org.example.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // Правильный формат JDBC URL для neon.tech
        val jdbcUrl = "jdbc:postgresql://ep-tiny-voice-aqt8dubt.c-8.us-east-1.aws.neon.tech:5432/neondb?sslmode=require&user=neondb_owner&password=npg_bN9rEUyhBXI3"

        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 5
            minimumIdle = 1
            connectionTimeout = 30000
            maxLifetime = 1800000
            idleTimeout = 600000
            isAutoCommit = false
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Users, Prizes, Laureates, UserFavorites
            )
            SeedData.insertPrizes()
        }
    }
}