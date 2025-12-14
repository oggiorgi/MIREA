package com.example.firstapplication.domain.repository

import com.example.firstapplication.domain.model.TodoItem

interface TodoRepository {
    suspend fun getTodos(): List<TodoItem>
    suspend fun toggleTodo(id: Int)
}

