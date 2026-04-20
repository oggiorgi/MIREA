package com.example.firstapplication

import android.app.Application
import com.example.firstapplication.data.local.TodoDatabase
import com.example.firstapplication.data.preferences.UserPreferencesRepository
import com.example.firstapplication.data.repository.TodoRepositoryImpl
import com.example.firstapplication.local.TodoJsonDataSource

class TodoApp : Application() {

    private val database by lazy { TodoDatabase.getDatabase(this) }
    private val jsonDataSource by lazy { TodoJsonDataSource(this) }
    private val preferencesRepository by lazy { UserPreferencesRepository(this) }

    val repository by lazy {
        TodoRepositoryImpl(
            todoDao = database.todoDao(),
            jsonDataSource = jsonDataSource,
            preferencesRepository = preferencesRepository
        )
    }
}