package org.example.database

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun createUser(login: String, passwordHash: String, role: String = "user"): Int {
        return transaction {
            Users.insert {
                it[Users.login] = login
                it[Users.passwordHash] = passwordHash
                it[Users.role] = role
            } get Users.id
        }
    }

    fun findUserByLogin(login: String): User? {
        return transaction {
            Users.select { Users.login eq login }
                .map { row ->
                    User(
                        id = row[Users.id],
                        login = row[Users.login],
                        passwordHash = row[Users.passwordHash],
                        role = row[Users.role]
                    )
                }
                .singleOrNull()
        }
    }

    fun userExists(login: String): Boolean {
        return transaction {
            Users.select { Users.login eq login }.empty().not()
        }
    }
}

data class User(
    val id: Int,
    val login: String,
    val passwordHash: String,
    val role: String
)