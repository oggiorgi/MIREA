package com.example.photocatalog.presentation.ui.screens

import androidx.compose.foundation.layout.*
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
    onSuccess: (String) -> Unit  // ← УБРАТЬ @Composable
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by authViewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) {
            onSuccess((state as AuthState.Success).token)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { authViewModel.login(login, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

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