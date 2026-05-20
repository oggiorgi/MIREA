package com.example.photocatalog.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.photocatalog.presentation.viewmodel.AuthState
import com.example.photocatalog.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onSuccess: (String) -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by authViewModel.state.collectAsState()

    var ignoreFirstSuccess by remember { mutableStateOf(true) }

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            if (!ignoreFirstSuccess) {
                onSuccess((state as AuthState.Success).token)
            }
            ignoreFirstSuccess = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок
        Text(
            text = if (isLoginMode) "Вход" else "Регистрация",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Поле Логин
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Поле Email (только для регистрации)
        if (!isLoginMode) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Поле Пароль
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка действия
        Button(
            onClick = {
                if (isLoginMode) {
                    authViewModel.login(login, password)
                } else {
                    authViewModel.register(login, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
        }

        // Ссылка для переключения режима
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isLoginMode) "Зарегистрироваться" else "Уже есть аккаунт? Войти",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                isLoginMode = !isLoginMode
                // Очищаем ошибку при переключении
                if (state is AuthState.Error) {
                    // сброс состояния
                }
            }
        )

        // Статус загрузки/ошибки
        when (state) {
            is AuthState.Loading -> {
                Spacer(modifier = Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            is AuthState.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (state as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }
    }
}