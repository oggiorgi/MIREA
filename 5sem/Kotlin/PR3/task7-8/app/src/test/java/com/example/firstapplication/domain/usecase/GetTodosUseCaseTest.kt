package com.example.firstapplication.domain.usecase

import com.example.firstapplication.domain.model.TodoItem
import com.example.firstapplication.domain.repository.TodoRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTodosUseCaseTest {

    private lateinit var repository: TodoRepository
    private lateinit var getTodosUseCase: GetTodosUseCase

    @Before
    fun setup() {
        repository = mockk()
        getTodosUseCase = GetTodosUseCase(repository)
    }

    @Test
    fun `GetTodosUseCase возвращает 3 задачи`() = runBlocking {
        // Arrange
        val expectedTodos = listOf(
            TodoItem(
                id = 1,
                title = "Купить молоко",
                description = "2 литра, обезжиренное",
                isCompleted = false
            ),
            TodoItem(
                id = 2,
                title = "Позвонить маме",
                description = "Спросить про выходные",
                isCompleted = true
            ),
            TodoItem(
                id = 3,
                title = "Сделать ДЗ по Android",
                description = "Clean Architecture + Compose",
                isCompleted = false
            )
        )

        coEvery { repository.getTodos() } returns expectedTodos

        // Act
        val result = getTodosUseCase()

        // Assert
        assertEquals(3, result.size)
        assertEquals(expectedTodos, result)
        assertEquals("Купить молоко", result[0].title)
        assertEquals("Позвонить маме", result[1].title)
        assertEquals("Сделать ДЗ по Android", result[2].title)
    }
}

