package com.example.firstapplication.data.repository

import com.example.firstapplication.data.local.TodoJsonDataSource
import com.example.firstapplication.data.model.TodoItemDto
import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.repository.TodoRepository

class TodoRepositoryImpl(
    private val dataSource: TodoJsonDataSource
) : TodoRepository {
    
    private var todos: MutableList<TodoItem> = mutableListOf()
    private var isInitialized = false

    override suspend fun getTodos(): List<TodoItem> {
        if (!isInitialized) {
            val dtos = dataSource.getTodos()
            todos = dtos.map { dto ->
                TodoItem(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description,
                    isCompleted = dto.isCompleted
                )
            }.toMutableList()
            isInitialized = true
        }
        return todos.toList()
    }

    override suspend fun toggleTodo(id: Int) {
        val index = todos.indexOfFirst { it.id == id }
        if (index != -1) {
            val todo = todos[index]
            todos[index] = todo.copy(isCompleted = !todo.isCompleted)
        }
    }
}

