package com.example.photocatalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.photocatalog.data.local.DataStoreManager
import com.example.photocatalog.di.AppModule
import com.example.photocatalog.presentation.navigation.AppNavigation
import com.example.photocatalog.presentation.viewmodel.AuthViewModel
import com.example.photocatalog.presentation.viewmodel.AuthViewModelFactory
import com.example.photocatalog.presentation.viewmodel.LaureateViewModel
import com.example.photocatalog.presentation.viewmodel.LaureateViewModelFactory
import com.example.photocatalog.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppModule.init(applicationContext)
        DataStoreManager.init(applicationContext)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NobelPrizeApp()
                }
            }
        }
    }
}

@Composable
fun NobelPrizeApp() {
    // Получаем ViewModel через фабрики из AppModule
    val laureateViewModel: LaureateViewModel = viewModel(
        factory = LaureateViewModelFactory(
            getLaureatesUseCase = AppModule.provideGetLaureatesUseCase(),
            filterLaureatesUseCase = AppModule.provideFilterLaureatesUseCase(),
            addFavoriteUseCase = AppModule.provideAddFavoriteUseCase(),
            removeFavoriteUseCase = AppModule.provideRemoveFavoriteUseCase(),
            getFavoritesUseCase = AppModule.provideGetFavoritesUseCase(),
            tokenRepository = AppModule.provideTokenRepository()
        )
    )

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            loginUseCase = AppModule.provideLoginUseCase(),
            registerUseCase = AppModule.provideRegisterUseCase()
        )
    )

    val tokenRepository = AppModule.provideTokenRepository()

    AppNavigation(
        authViewModel = authViewModel,
        laureateViewModel = laureateViewModel,
        tokenRepository = tokenRepository
    )
}