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
        Log.d("TokenRepository", "Saving token: ${token.take(50)}...")
        dataStore.edit { prefs ->
            prefs[tokenKey] = token
        }
        // Проверка сохранения
        val saved = getTokenFlow().first()
        Log.d("TokenRepository", "Token saved and verified: ${saved?.take(50)}")
    }
    fun getTokenFlow(): Flow<String?> = dataStore.data.map { prefs ->
        prefs[tokenKey]
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(tokenKey) }
    }
}