package com.example.firstapplication.domain.usecase

import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTodosUseCase(
    private val repository: TodoRepository
) {
    operator fun invoke(): Flow<List<TodoItem>> {
        return repository.observeTodos()
    }
}