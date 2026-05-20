package com.example.photocatalog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photocatalog.data.local.TokenRepository
import com.example.photocatalog.presentation.ui.screens.LaureateDetailScreen
import com.example.photocatalog.presentation.ui.screens.LaureateListScreen
import com.example.photocatalog.presentation.ui.screens.LoginScreen
import com.example.photocatalog.presentation.viewmodel.AuthViewModel
import com.example.photocatalog.presentation.viewmodel.LaureateViewModel
import com.example.photocatalog.presentation.viewmodel.UiState
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    laureateViewModel: LaureateViewModel,
    tokenRepository: TokenRepository
) {
    val navController = rememberNavController()
    val tokenFlow = tokenRepository.getTokenFlow()
    val token by tokenFlow.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = if (token.isNullOrEmpty()) "login" else "list"
    ) {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onSuccess = { newToken ->
                    // ✅ Используем coroutineScope вместо LaunchedEffect
                    coroutineScope.launch {
                        tokenRepository.saveToken(newToken)
                        navController.navigate("list") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("list") {
            LaureateListScreen(
                viewModel = laureateViewModel,
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

            val uiState by laureateViewModel.uiState.collectAsState()
            val laureates = if (uiState is UiState.Success) {
                (uiState as UiState.Success).laureates.filter {
                    it.year == year && it.category == category
                }
            } else emptyList()

            LaureateDetailScreen(
                year = year,
                category = category,
                laureates = laureates,
                viewModel = laureateViewModel
            )
        }
    }
}