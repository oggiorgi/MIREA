package com.example.firstapplication.domain.repository

import com.example.firstapplication.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeTodos(): Flow<List<TodoItem>>
    suspend fun seedFromJsonIfNeeded()
    suspend fun getTodoById(id: Int): TodoItem?
    suspend fun addTodo(title: String, description: String)
    suspend fun updateTodo(id: Int, title: String, description: String, isCompleted: Boolean)
    suspend fun deleteTodo(id: Int)
    suspend fun toggleTodo(id: Int)

    fun observeCompletedTasksColorEnabled(): Flow<Boolean>
    suspend fun setCompletedTasksColorEnabled(enabled: Boolean)
}