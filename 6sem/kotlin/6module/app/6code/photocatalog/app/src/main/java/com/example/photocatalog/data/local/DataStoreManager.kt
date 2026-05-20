package com.example.photocatalog.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore("auth")

object DataStoreManager {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getDataStore(): DataStore<Preferences> {
        return appContext.dataStore
    }
}