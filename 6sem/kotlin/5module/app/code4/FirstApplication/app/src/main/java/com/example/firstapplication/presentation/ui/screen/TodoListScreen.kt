package com.example.firstapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.firstapplication.presentation.ui.component.TodoItemCard
import com.example.firstapplication.presentation.viewmodel.TodoViewModel
import com.example.firstapplication.ui.theme.RavitasNeegular


private val DarkPurple = Color(0xFF1E1C2F)
private val DeepPurple = Color(0xFF2D1E36)
private val RichPurple = Color(0xFF3E0866)
private val BrightPurple = Color(0xFF501A5E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onAddTodo: () -> Unit,
    onOpenDetail: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkPurple,  // Фон главного экрана
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Todo List",
                        color = Color.White,

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepPurple,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "Цвет завершенных",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Switch(
                            checked = state.completedTasksColorEnabled,
                            onCheckedChange = viewModel::onCompletedTasksColorChanged,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = BrightPurple,
                                checkedTrackColor = RichPurple,
                                uncheckedThumbColor = DeepPurple,
                                uncheckedTrackColor = DarkPurple
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTodo,
                containerColor = BrightPurple,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить задачу"
                )
            }
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrightPurple)
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error ?: "Ошибка",
                        color = Color.White
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(12.dp)
                        .testTag("todo_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.todos, key = { it.id }) { item ->
                        TodoItemCard(
                            item = item,
                            highlightCompleted = state.completedTasksColorEnabled,
                            onClick = { onOpenDetail(item.id) },
                            onToggle = { viewModel.onToggle(item.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}