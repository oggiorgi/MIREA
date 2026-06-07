package com.example.museflow.presentation.ui.auth

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

/*
 * Экран авторизации.
 * Поддерживает два режима: вход и регистрация. Управляет локальным состоянием 
 * полей ввода и синхронизируется с AuthViewModel для выполнения запросов.
 */
@Composable
fun AuthScreen(
    onSuccess: (String) -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    var isLoginMode by remember { mutableStateOf(true) }
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by authViewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var firstSuccessConsumed by remember { mutableStateOf(false) }

    /*
     * Сброс состояния при каждом показе экрана. 
     * Это гарантирует, что пользователь всегда начинает с чистых полей и Idle-стейта.
     */
    LaunchedEffect(Unit) {
        authViewModel.resetState()
        login = ""
        email = ""
        password = ""
        isLoginMode = true
        firstSuccessConsumed = false
    }

    /*
     * Обработка изменений состояния аутентификации. 
     * Успех триггерит переход на главный экран, ошибки отображаются через Toast.
     */
    LaunchedEffect(state) {
        if (state is AuthState.Success && !firstSuccessConsumed) {
            firstSuccessConsumed = true
            onSuccess((state as AuthState.Success).token)
        } else if (state is AuthState.Error) {
            Toast.makeText(context, (state as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.resetState()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLoginMode) "Вход" else "Регистрация",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (!isLoginMode) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (isLoginMode) authViewModel.login(login, password)
                        else authViewModel.register(login, email, password)
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isLoginMode) {
                        if (login.isNotBlank() && password.isNotBlank()) {
                            authViewModel.login(login, password)
                        }
                    } else {
                        if (login.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            authViewModel.register(login, email, password)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = if (isLoginMode) login.isNotBlank() && password.isNotBlank() 
                          else login.isNotBlank() && email.isNotBlank() && password.isNotBlank()
            ) {
                Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLoginMode) "Нет аккаунта? Зарегистрироваться" else "Уже есть аккаунт? Войти",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    isLoginMode = !isLoginMode
                    // Очищаем ошибки при переключении режима
                    firstSuccessConsumed = false
                }
            )

            when (state) {
                is AuthState.Loading -> CircularProgressIndicator()
                else -> Unit
            }
        }
    }
}