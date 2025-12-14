package com.example.firstapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.firstapplication.presentation.ui.screen.TodoDetailScreen
import com.example.firstapplication.presentation.ui.screen.TodoListScreen
import com.example.firstapplication.presentation.viewmodel.TodoViewModel

sealed class Screen(val route: String) {
    object TodoList : Screen("todo_list")
    object TodoDetail : Screen("todo_detail/{todoId}") {
        fun createRoute(todoId: Int) = "todo_detail/$todoId"
    }
}

@Composable
fun TodoNavigation(
    navController: NavHostController,
    viewModel: TodoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TodoList.route
    ) {
        composable(Screen.TodoList.route) {
            TodoListScreen(
                viewModel = viewModel,
                onItemClick = { todoId ->
                    navController.navigate(Screen.TodoDetail.createRoute(todoId))
                }
            )
        }
        
        composable(Screen.TodoDetail.route) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toIntOrNull() ?: 0
            TodoDetailScreen(
                todoId = todoId,
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

