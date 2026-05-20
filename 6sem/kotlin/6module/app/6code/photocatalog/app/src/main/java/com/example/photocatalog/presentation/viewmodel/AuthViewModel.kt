package com.example.photocatalog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.domain.usecase.LoginUseCase
import com.example.photocatalog.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val token = loginUseCase(login, password)
                _state.value = AuthState.Success(token)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(login: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val token = registerUseCase(login, email, password)
                _state.value = AuthState.Success(token)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }
}