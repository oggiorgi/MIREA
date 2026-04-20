package com.example.firstapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.firstapplication.presentation.viewmodel.TodoViewModel

// Цвета
private val DarkPurple = Color(0xFF1E1C2F)
private val DeepPurple = Color(0xFF2D1E36)
private val RichPurple = Color(0xFF3E0866)
private val BrightPurple = Color(0xFF501A5E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Int,
    viewModel: TodoViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val existingItem = state.todos.firstOrNull { it.id == todoId }
    val isEditMode = todoId != 0

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(existingItem, isEditMode) {
        if (isEditMode && existingItem != null && !initialized) {
            title = existingItem.title
            description = existingItem.description
            isCompleted = existingItem.isCompleted
            initialized = true
        }
    }

    Scaffold(
        containerColor = DarkPurple,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Редактирование задачи" else "Новая задача",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isEditMode && existingItem == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .testTag("todo_detail")
            ) {
                Text(
                    text = "Задача не найдена",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("todo_detail"),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detail_title"),
                label = { Text(text = "Название", color = Color.White.copy(alpha = 0.7f)) },
                singleLine = true,
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Описание (опционально)", color = Color.White.copy(alpha = 0.7f)) },
                minLines = 4,
                textStyle = TextStyle(color = Color.White)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (isEditMode) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = BrightPurple,
                            uncheckedColor = Color.White
                        )
                    )
                    Text(
                        text = "Задача выполнена",
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("cancel_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepPurple
                    )
                ) {
                    Text(text = "Отмена", color = Color.White)
                }

                Button(
                    onClick = {
                        if (isEditMode) {
                            viewModel.updateTodo(
                                id = todoId,
                                title = title,
                                description = description,
                                isCompleted = isCompleted,
                                onDone = onBack
                            )
                        } else {
                            viewModel.addTodo(
                                title = title,
                                description = description,
                                onDone = onBack
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrightPurple,
                        disabledContainerColor = DeepPurple
                    )
                ) {
                    Text(text = if (isEditMode) "Сохранить" else "Добавить", color = Color.White)
                }
            }

            if (isEditMode) {
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { viewModel.deleteTodo(todoId, onBack) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = RichPurple
                    )
                ) {
                    Text(text = "Удалить")
                }
            }
        }
    }
}