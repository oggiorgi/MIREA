package com.example.firstapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.firstapplication.data.local.TodoJsonDataSource
import com.example.firstapplication.data.repository.TodoRepositoryImpl
import com.example.firstapplication.domain.usecase.GetTodosUseCase
import com.example.firstapplication.domain.usecase.ToggleTodoUseCase
import com.example.firstapplication.navigation.TodoNavigation
import com.example.firstapplication.presentation.viewmodel.TodoViewModel
import com.example.firstapplication.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        
        // Инициализация зависимостей
        val dataSource = TodoJsonDataSource(this)
        val repository = TodoRepositoryImpl(dataSource)
        val getTodosUseCase = GetTodosUseCase(repository)
        val toggleTodoUseCase = ToggleTodoUseCase(repository)
        val viewModel = TodoViewModel(getTodosUseCase, toggleTodoUseCase)
        
        setContent {
            AppTheme {
                TodoApp(viewModel = viewModel)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
}

@Composable
fun TodoApp(viewModel: TodoViewModel) {
    val navController = rememberNavController()
    TodoNavigation(navController = navController, viewModel = viewModel)
}

