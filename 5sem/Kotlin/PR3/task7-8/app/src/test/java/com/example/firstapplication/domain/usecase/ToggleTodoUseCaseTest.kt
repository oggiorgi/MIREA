package com.example.firstapplication.domain.usecase

import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ToggleTodoUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var toggleTodoUseCase: ToggleTodoUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        toggleTodoUseCase = ToggleTodoUseCase(repository)
    }

    @Test
    fun `toggleTodo меняет isCompleted`() = runBlocking {
        // Arrange
        val todoId = 1
        var isCompleted = false
        
        // Настраиваем мок: при вызове toggleTodo меняем состояние
        coEvery { repository.toggleTodo(todoId) } answers {
            isCompleted = !isCompleted
        }

        // Act & Assert
        // Проверяем начальное состояние
        assertFalse("Начальное состояние должно быть false", isCompleted)
        
        // Вызываем toggleTodo
        toggleTodoUseCase(todoId)
        
        // Проверяем, что метод был вызван
        coVerify(exactly = 1) { repository.toggleTodo(todoId) }
        
        // Проверяем, что isCompleted изменился
        assertTrue("isCompleted должен измениться на true", isCompleted)
        
        // Вызываем toggleTodo еще раз
        toggleTodoUseCase(todoId)
        
        // Проверяем, что isCompleted снова изменился
        assertFalse("isCompleted должен измениться обратно на false", isCompleted)
        coVerify(exactly = 2) { repository.toggleTodo(todoId) }
    }

    @Test
    fun `toggleTodo вызывает repository toggleTodo с правильным id`() = runBlocking {
        // Arrange
        val todoId = 2

        // Act
        toggleTodoUseCase(todoId)

        // Assert
        coVerify(exactly = 1) { repository.toggleTodo(todoId) }
    }
}

