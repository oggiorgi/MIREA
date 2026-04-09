# Результаты Unit-тестов

## Тест: GetTodosUseCaseTest

### Описание теста
Тест проверяет, что `GetTodosUseCase` корректно возвращает 3 задачи из репозитория.

### Структура теста

**Файл:** `app/src/test/java/com/example/firstapplication/domain/usecase/GetTodosUseCaseTest.kt`

**Метод теста:** `GetTodosUseCase возвращает 3 задачи`

### Что проверяется:

1. ✅ Use case возвращает ровно 3 задачи
2. ✅ Данные задач соответствуют ожидаемым значениям
3. ✅ Названия задач корректны:
   - "Купить молоко"
   - "Позвонить маме"
   - "Сделать ДЗ по Android"

### Технологии:

- **MockK** - для создания моков репозитория
- **JUnit 4** - для фреймворка тестирования
- **Kotlin Coroutines** - для тестирования suspend функций

### Как запустить тест:

#### Через Android Studio:
1. Откройте файл `GetTodosUseCaseTest.kt`
2. Нажмите на зеленую стрелку рядом с классом или методом теста
3. Выберите "Run 'GetTodosUseCaseTest'"

#### Через Gradle (если Java версия совместима):
```bash
./gradlew test
```

#### Просмотр результатов:
После запуска результаты будут отображены в окне "Run" в Android Studio:
- ✅ Зеленая галочка - тест прошел успешно
- ❌ Красный крестик - тест не прошел

### Ожидаемый результат:

```
✅ GetTodosUseCaseTest
  ✅ GetTodosUseCase возвращает 3 задачи
```

### Код теста:

```kotlin
@Test
fun `GetTodosUseCase возвращает 3 задачи`() = runBlocking {
    // Arrange
    val expectedTodos = listOf(
        TodoItem(id = 1, title = "Купить молоко", ...),
        TodoItem(id = 2, title = "Позвонить маме", ...),
        TodoItem(id = 3, title = "Сделать ДЗ по Android", ...)
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
```

