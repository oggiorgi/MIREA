package com.example.firstapplication.domain.usecase

import com.example.firstapplication.domain.repository.TodoRepository

class ToggleTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(id: Int) = repository.toggleTodo(id)
}

