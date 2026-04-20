package com.example.firstapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.firstapplication.domain.usecase.TodoUseCases
import com.example.firstapplication.navigation.TodoNavGraph // Или TodoNavigation
import com.example.firstapplication.presentation.viewmodel.TodoViewModel
import com.example.firstapplication.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Берем репозиторий из приложения
        val repository = (application as TodoApp).repository

        // Создаем UseCases
        val useCases = TodoUseCases(repository)

        // Фабрика для ViewModel
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TodoViewModel(useCases) as T
            }
        }

        // Получаем ViewModel
        val viewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]

        setContent {
            AppTheme() {
                // Убедись, что функция называется TodoNavGraph или TodoNavigation
                TodoNavGraph(viewModel = viewModel)
            }
        }
    }
}