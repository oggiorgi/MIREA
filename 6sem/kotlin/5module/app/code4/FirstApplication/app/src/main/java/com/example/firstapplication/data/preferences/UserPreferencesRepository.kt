package com.example.firstapplication.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFERENCES_NAME = "todo_preferences"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

class UserPreferencesRepository(private val context: Context) {

    private companion object {
        val COMPLETED_TASKS_COLOR_ENABLED = booleanPreferencesKey("completed_tasks_color_enabled")
    }

    val completedTasksColorEnabled: Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[COMPLETED_TASKS_COLOR_ENABLED] ?: true
        }

    suspend fun setCompletedTasksColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TASKS_COLOR_ENABLED] = enabled
        }
    }
}