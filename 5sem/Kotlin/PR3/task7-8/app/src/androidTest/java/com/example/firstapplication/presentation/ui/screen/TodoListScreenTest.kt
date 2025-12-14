package com.example.firstapplication.presentation.ui.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.firstapplication.data.local.TodoJsonDataSource
import com.example.firstapplication.data.repository.TodoRepositoryImpl
import com.example.firstapplication.domain.usecase.GetTodosUseCase
import com.example.firstapplication.domain.usecase.ToggleTodoUseCase
import com.example.firstapplication.presentation.viewmodel.TodoViewModel
import com.example.firstapplication.ui.theme.AppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class TodoListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: TodoViewModel

    @Before
    fun setup() {
        // Инициализация ViewModel с реальными данными из JSON
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dataSource = TodoJsonDataSource(context)
        val repository = TodoRepositoryImpl(dataSource)
        val getTodosUseCase = GetTodosUseCase(repository)
        val toggleTodoUseCase = ToggleTodoUseCase(repository)
        viewModel = TodoViewModel(getTodosUseCase, toggleTodoUseCase)
    }

    @Test
    fun отображаются_все_3_задачи_из_JSON() {
        // Устанавливаем контент с ViewModel
        composeTestRule.setContent {
            AppTheme {
                TodoListScreen(
                    viewModel = viewModel,
                    onItemClick = {}
                )
            }
        }

        // Ждем загрузки данных из JSON и рендеринга UI
        Thread.sleep(1500)

        // Проверяем, что отображается заголовок экрана
        composeTestRule.onNodeWithText("Список задач")
            .assertIsDisplayed()

        // Проверяем, что отображаются все 3 задачи из JSON
        // Задача 1: "Купить молоко"
        composeTestRule.onNodeWithText("Купить молоко")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("2 литра, обезжиренное")
            .assertIsDisplayed()

        // Задача 2: "Позвонить маме"
        composeTestRule.onNodeWithText("Позвонить маме")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Спросить про выходные")
            .assertIsDisplayed()

        // Задача 3: "Сделать ДЗ по Android"
        composeTestRule.onNodeWithText("Сделать ДЗ по Android")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Clean Architecture + Compose")
            .assertIsDisplayed()
    }

    @Test
    fun чекбокс_переключает_статус() {
        // Устанавливаем контент с ViewModel
        composeTestRule.setContent {
            AppTheme {
                TodoListScreen(
                    viewModel = viewModel,
                    onItemClick = {}
                )
            }
        }

        // Ждем загрузки данных из JSON и рендеринга UI
        Thread.sleep(1500)

        // Находим задачу "Купить молоко" (начальное состояние: isCompleted = false)
        composeTestRule.onNodeWithText("Купить молоко")
            .assertIsDisplayed()

        // Находим все чекбоксы с состоянием Off и выбираем первый (для задачи "Купить молоко")
        val allUncheckedCheckboxes = composeTestRule.onAllNodes(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ToggleableState,
                ToggleableState.Off
            )
        )

        // Выбираем первый чекбокс (для первой задачи)
        val firstCheckbox = allUncheckedCheckboxes.onFirst()
        firstCheckbox.assertIsDisplayed()

        // Кликаем на чекбокс
        firstCheckbox.performClick()

        // Ждем обновления UI после клика
        Thread.sleep(1000)

        // Проверяем, что чекбокс теперь отмечен
        // Ищем все чекбоксы с состоянием On и выбираем первый
        val allCheckedCheckboxes = composeTestRule.onAllNodes(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ToggleableState,
                ToggleableState.On
            )
        )
        val checkedCheckbox = allCheckedCheckboxes.onFirst()
        checkedCheckbox.assertIsDisplayed()

        // Кликаем еще раз для переключения обратно
        checkedCheckbox.performClick()

        // Ждем обновления UI
        Thread.sleep(1000)

        // Проверяем, что чекбокс снова не отмечен
        // Ищем все чекбоксы с состоянием Off и выбираем первый
        val allUncheckedCheckboxesAfter = composeTestRule.onAllNodes(
            SemanticsMatcher.expectValue(
                SemanticsProperties.ToggleableState,
                ToggleableState.Off
            )
        )
        val uncheckedCheckbox = allUncheckedCheckboxesAfter.onFirst()
        uncheckedCheckbox.assertIsDisplayed()
    }
}

