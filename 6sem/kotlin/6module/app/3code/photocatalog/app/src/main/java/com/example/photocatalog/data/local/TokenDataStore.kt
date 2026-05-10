package com.example.photocatalog.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class TokenDataStore(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }
    
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun clearToken() {
        println("🔴 CLEARING TOKEN")
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
        println("✅ TOKEN CLEARED, new token: ${getToken()}")
    }

    suspend fun getToken(): String? {
        val token = context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.firstOrNull()
        println("🔵 GET TOKEN: ${if (token != null) "exists (${token.take(20)}...)" else "null"}")
        return token
    }
    
    fun getTokenFlow(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }
}