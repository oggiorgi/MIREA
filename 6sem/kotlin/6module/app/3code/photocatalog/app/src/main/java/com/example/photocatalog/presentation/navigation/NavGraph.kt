package com.example.photocatalog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photocatalog.di.AppModule
import com.example.photocatalog.presentation.auth.LoginScreen
import com.example.photocatalog.presentation.auth.LoginViewModel
import com.example.photocatalog.presentation.auth.LoginViewModelFactory
import com.example.photocatalog.presentation.userdetail.UserDetailScreen
import com.example.photocatalog.presentation.userslist.UsersListScreen
import com.example.photocatalog.presentation.userslist.UsersListViewModel
import com.example.photocatalog.presentation.userslist.UsersListViewModelFactory

@Composable
fun NavGraph() {
    val context = LocalContext.current
    val appModule = AppModule(context)
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(appModule.loginUseCase)
            )
            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("users_list") {
            val viewModel: UsersListViewModel = viewModel(
                factory = UsersListViewModelFactory(
                    appModule.getUsersUseCase,
                    appModule.logoutUseCase
                )
            )
            UsersListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            "user_detail/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1

            UserDetailScreen(
                navController = navController,
                userId = userId,
                getUserDetailUseCase = appModule.getUserDetailUseCase,
                logoutUseCase = appModule.logoutUseCase
            )
        }
    }
}