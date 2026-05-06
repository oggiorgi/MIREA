package com.example.photocatalog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photocatalog.presentation.screen.PhotoDetailScreen
import com.example.photocatalog.presentation.screen.PhotoListScreen
import com.example.photocatalog.presentation.screen.PhotoListState
import com.example.photocatalog.presentation.screen.PhotoListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photocatalog.di.AppModule

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    // Создаем ViewModel только здесь, один раз
    val viewModel: PhotoListViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return PhotoListViewModel(AppModule.getPhotosUseCase) as T
            }
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()
    val photos = if (state is PhotoListState.Success) {
        (state as PhotoListState.Success).photos
    } else {
        emptyList()
    }

    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            PhotoListScreen(
                navController = navController,
                viewModel = viewModel  // Передаем существующий ViewModel
            )
        }

        composable(
            route = "detail/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
            val photo = photos.find { it.id == photoId }

            if (photo != null) {
                PhotoDetailScreen(
                    navController = navController,
                    photo = photo
                )
            }
        }
    }
}