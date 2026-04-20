package com.example.firstapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.usecase.TodoUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TodoUiState(
    val isLoading: Boolean = true,
    val todos: List<TodoItem> = emptyList(),
    val completedTasksColorEnabled: Boolean = true,
    val error: String? = null
)

class TodoViewModel(
    private val useCases: TodoUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            runCatching { useCases.seedFromJsonIfNeeded() }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "Ошибка загрузки")
                    }
                }
        }

        viewModelScope.launch {
            useCases.observeTodos().collect { todos ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todos = todos,
                        error = null
                    )
                }
            }
        }

        viewModelScope.launch {
            useCases.observeCompletedTasksColorEnabled().collect { enabled ->
                _uiState.update { it.copy(completedTasksColorEnabled = enabled) }
            }
        }
    }

    fun onToggle(id: Int) {
        viewModelScope.launch {
            useCases.toggleTodo(id)
        }
    }

    fun onCompletedTasksColorChanged(enabled: Boolean) {
        viewModelScope.launch {
            useCases.setCompletedTasksColorEnabled(enabled)
        }
    }

    fun addTodo(
        title: String,
        description: String,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            if (title.isBlank()) return@launch
            useCases.addTodo(title, description)
            onDone()
        }
    }

    fun updateTodo(
        id: Int,
        title: String,
        description: String,
        isCompleted: Boolean,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            if (title.isBlank()) return@launch
            useCases.updateTodo(id, title, description, isCompleted)
            onDone()
        }
    }

    fun deleteTodo(id: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            useCases.deleteTodo(id)
            onDone()
        }
    }
}