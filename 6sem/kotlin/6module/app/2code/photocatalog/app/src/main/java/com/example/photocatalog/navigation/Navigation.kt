package com.example.photocatalog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photocatalog.presentation.ui.screens.LaureateDetailScreen
import com.example.photocatalog.presentation.ui.screens.LaureateListScreen
import com.example.photocatalog.presentation.viewmodel.LaureateViewModel
import com.example.photocatalog.presentation.viewmodel.UiState

@Composable
fun AppNavigation(viewModel: LaureateViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            LaureateListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { year, category ->
                    navController.navigate("detail/$year/$category")
                }
            )
        }

        composable(
            route = "detail/{year}/{category}",
            arguments = listOf(
                navArgument("year") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val year = backStackEntry.arguments?.getString("year") ?: ""
            val category = backStackEntry.arguments?.getString("category") ?: ""

            // Получаем ВСЕХ лауреатов за этот год и категорию
            val laureates = if (uiState is UiState.Success) {
                (uiState as UiState.Success).laureates.filter {
                    it.year == year && it.category == category
                }
            } else emptyList()

            LaureateDetailScreen(year = year, category = category, laureates = laureates)
        }
    }
}
