package com.example.museflow.data.network.auth

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Менеджер безопасности и сессий.
 * Отвечает за защищенное хранение JWT токена и данных профиля пользователя 
 * в SharedPreferences. Используется во всем приложении для проверки статуса авторизации.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("jwt_token", token).apply()
    }

    fun saveUsername(username: String) {
        prefs.edit().putString("username", username).apply()
    }

    fun getToken(): String? = prefs.getString("jwt_token", null)

    fun getUsername(): String? = prefs.getString("username", null)

    fun clearToken() {
        prefs.edit().remove("jwt_token").remove("username").apply()
    }
}