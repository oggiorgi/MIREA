package com.example.firstapplication.data.repository

import com.example.firstapplication.data.local.TodoDao
import com.example.firstapplication.data.local.TodoEntity
import com.example.firstapplication.data.model.toDomain
import com.example.firstapplication.data.model.toEntity
import com.example.firstapplication.data.preferences.UserPreferencesRepository
import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.repository.TodoRepository
import com.example.firstapplication.local.TodoJsonDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val jsonDataSource: TodoJsonDataSource,
    private val preferencesRepository: UserPreferencesRepository
) : TodoRepository {

    override fun observeTodos(): Flow<List<TodoItem>> {
        return todoDao.observeTodos().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun seedFromJsonIfNeeded() {
        if (todoDao.getCount() > 0) return

        val todos = jsonDataSource.getTodos().map { it.toEntity() }
        todoDao.insertAll(todos)
    }

    override suspend fun getTodoById(id: Int): TodoItem? {
        return todoDao.getTodoById(id)?.toDomain()
    }

    override suspend fun addTodo(title: String, description: String) {
        todoDao.insert(
            TodoEntity(
                title = title.trim(),
                description = description.trim(),
                isCompleted = false
            )
        )
    }

    override suspend fun updateTodo(
        id: Int,
        title: String,
        description: String,
        isCompleted: Boolean
    ) {
        todoDao.update(
            TodoEntity(
                id = id,
                title = title.trim(),
                description = description.trim(),
                isCompleted = isCompleted
            )
        )
    }

    override suspend fun deleteTodo(id: Int) {
        todoDao.deleteById(id)
    }

    override suspend fun toggleTodo(id: Int) {
        val current = todoDao.getTodoById(id) ?: return
        todoDao.update(current.copy(isCompleted = !current.isCompleted))
    }

    override fun observeCompletedTasksColorEnabled(): Flow<Boolean> {
        return preferencesRepository.completedTasksColorEnabled
    }

    override suspend fun setCompletedTasksColorEnabled(enabled: Boolean) {
        preferencesRepository.setCompletedTasksColorEnabled(enabled)
    }
}