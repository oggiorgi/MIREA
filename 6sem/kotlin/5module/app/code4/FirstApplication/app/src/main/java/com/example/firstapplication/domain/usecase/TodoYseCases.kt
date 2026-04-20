package com.example.firstapplication.domain.usecase

import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class TodoUseCases(
    private val repository: TodoRepository
) {
    fun observeTodos(): Flow<List<TodoItem>> = repository.observeTodos()

    suspend fun seedFromJsonIfNeeded() = repository.seedFromJsonIfNeeded()

    suspend fun getTodoById(id: Int): TodoItem? = repository.getTodoById(id)

    suspend fun addTodo(title: String, description: String) =
        repository.addTodo(title, description)

    suspend fun updateTodo(
        id: Int,
        title: String,
        description: String,
        isCompleted: Boolean
    ) = repository.updateTodo(id, title, description, isCompleted)

    suspend fun deleteTodo(id: Int) = repository.deleteTodo(id)

    suspend fun toggleTodo(id: Int) = repository.toggleTodo(id)

    fun observeCompletedTasksColorEnabled(): Flow<Boolean> =
        repository.observeCompletedTasksColorEnabled()

    suspend fun setCompletedTasksColorEnabled(enabled: Boolean) =
        repository.setCompletedTasksColorEnabled(enabled)
}