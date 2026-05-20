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
import com.example.photocatalog.data.repository.NobelPrizeRepositoryImpl
import com.example.photocatalog.domain.usecase.FilterLaureatesUseCase
import com.example.photocatalog.domain.usecase.GetLaureatesUseCase
import com.example.photocatalog.presentation.navigation.AppNavigation
import com.example.photocatalog.presentation.viewmodel.LaureateViewModel
import com.example.photocatalog.presentation.viewmodel.LaureateViewModelFactory
import com.example.photocatalog.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val repository = NobelPrizeRepositoryImpl()
    val getLaureatesUseCase = GetLaureatesUseCase(repository)
    val filterLaureatesUseCase = FilterLaureatesUseCase()

    val factory = LaureateViewModelFactory(
        getLaureatesUseCase = getLaureatesUseCase,
        filterLaureatesUseCase = filterLaureatesUseCase
    )

    val viewModel: LaureateViewModel = viewModel(factory = factory)

    AppNavigation(viewModel = viewModel)
}