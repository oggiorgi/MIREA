package org.example.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    // Хешируем пароль
    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    // Проверяем пароль
    fun verify(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }
}