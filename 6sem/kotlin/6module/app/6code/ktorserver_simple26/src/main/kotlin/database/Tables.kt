package org.example.database

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val login = varchar("login", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 20).default("user")

    override val primaryKey = PrimaryKey(id)
}

object Prizes : Table("prizes") {
    val id = varchar("id", 50)  // внешний ID из Nobel API
    val awardYear = integer("award_year")
    val category = varchar("category", 50)
    val categoryFullName = varchar("category_full_name", 100)
    val prizeAmount = integer("prize_amount").nullable()

    override val primaryKey = PrimaryKey(id)
}

object Laureates : Table("laureates") {
    val id = varchar("id", 50)
    val prizeId = varchar("prize_id", 50).references(Prizes.id)
    val fullName = varchar("full_name", 255)
    val motivation = text("motivation")
    val share = integer("share")
    val portraitUrl = varchar("portrait_url", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

object UserFavorites : Table("user_favorites") {
    val userId = integer("user_id").references(Users.id)
    val prizeId = varchar("prize_id", 50).references(Prizes.id)
    val addedAt = long("added_at")  // timestamp

    override val primaryKey = PrimaryKey(userId, prizeId)
}