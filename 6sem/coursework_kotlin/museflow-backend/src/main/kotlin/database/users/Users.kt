package org.example.database.users

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter

object Users : IntIdTable("users") {
    val login = varchar("login", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at").defaultExpression(org.jetbrains.exposed.sql.javatime.CurrentDateTime)

    fun fetchUser(login: String): UserDTO? = transaction {
        Users.select { Users.login eq login }
            .singleOrNull()
            ?.toDTO()
    }

    fun fetchUserByEmail(email: String): UserDTO? = transaction {
        Users.select { Users.email eq email }
            .singleOrNull()
            ?.toDTO()
    }

    // ДОБАВЛЯЕМ метод create
    fun create(login: String, email: String, passwordHash: String): UserDTO? = transaction {
        val id = Users.insertAndGetId {
            it[Users.login] = login
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
        }
        Users.select { Users.id eq id }.singleOrNull()?.toDTO()
    }

    private fun ResultRow.toDTO(): UserDTO = UserDTO(
        id = this[Users.id].value,
        login = this[Users.login],
        email = this[Users.email],
        passwordHash = this[Users.passwordHash],
        createdAt = this[Users.createdAt].format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    fun getById(userId: Int): UserDTO? = transaction {
        Users.select { Users.id eq userId }
            .singleOrNull()
            ?.toDTO()
    }
}