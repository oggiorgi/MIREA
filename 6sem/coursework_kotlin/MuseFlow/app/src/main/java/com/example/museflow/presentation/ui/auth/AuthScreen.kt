package com.example.museflow.presentation.ui.auth

import androidx.compose.foundation.clickable  // ← ДОБАВИТЬ ЭТОТ ИМПОРТ
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onSuccess: (String) -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by authViewModel.state.collectAsState()

    var firstSuccessConsumed by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is AuthState.Success && !firstSuccessConsumed) {
            firstSuccessConsumed = true
            onSuccess((state as AuthState.Success).token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLoginMode) "Вход" else "Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = login, onValueChange = { login = it }, label = { Text("Логин") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        if (!isLoginMode) {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (isLoginMode) authViewModel.login(login, password)
            else authViewModel.register(login, email, password)
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isLoginMode) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { isLoginMode = !isLoginMode }
        )

        when (state) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text(text = (state as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
            else -> Unit
        }
    }
}