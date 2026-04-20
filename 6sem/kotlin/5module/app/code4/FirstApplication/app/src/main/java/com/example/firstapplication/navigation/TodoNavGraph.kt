package com.example.firstapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.firstapplication.presentation.ui.screen.TodoDetailScreen
import com.example.firstapplication.presentation.ui.screen.TodoListScreen
import com.example.firstapplication.presentation.viewmodel.TodoViewModel

@Composable
fun TodoNavGraph(viewModel: TodoViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(Routes.LIST) {
            TodoListScreen(
                viewModel = viewModel,
                onAddTodo = { navController.navigate("${Routes.DETAIL}/0") },
                onOpenDetail = { id -> navController.navigate("${Routes.DETAIL}/$id") }
            )
        }

        composable(
            route = Routes.DETAIL_WITH_ARG,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0

            TodoDetailScreen(
                todoId = id,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}