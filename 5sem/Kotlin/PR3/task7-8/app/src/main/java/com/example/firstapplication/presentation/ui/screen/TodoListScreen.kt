package com.example.firstapplication.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.firstapplication.presentation.ui.component.TodoItemCard
import com.example.firstapplication.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onItemClick: (Int) -> Unit
) {
    val todos by viewModel.todos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список задач") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = todos,
                key = { it.id }
            ) { todo ->
                TodoItemCard(
                    todo = todo,
                    onItemClick = onItemClick,
                    onCheckboxClick = { id ->
                        viewModel.toggleTodo(id)
                    }
                )
            }
        }
    }
}

