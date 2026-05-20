package com.example.photocatalog.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenRepository(private val context: Context) {
    private val dataStore = DataStoreManager.getDataStore()
    private val tokenKey = stringPreferencesKey("jwt_token")

    suspend fun saveToken(token: String) {
        Log.d("TokenRepository", "=== SAVING TOKEN ===")
        Log.d("TokenRepository", "Token: ${token.take(50)}...")
        dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
        val saved = getTokenFlow().first()
        Log.d("TokenRepository", "Verified saved: ${saved?.take(50)}")
    }

    fun getTokenFlow(): Flow<String?> = dataStore.data.map { prefs ->
        val token = prefs[tokenKey]
        Log.d("TokenRepository", "getTokenFlow: ${token?.take(50)}")
        token
    }

    suspend fun clearToken() {
        Log.d("TokenRepository", "=== CLEARING TOKEN ===")
        dataStore.edit { it.remove(tokenKey) }
        val cleared = getTokenFlow().first()
        Log.d("TokenRepository", "Token after clear: ${cleared ?: "null"}")
    }
}