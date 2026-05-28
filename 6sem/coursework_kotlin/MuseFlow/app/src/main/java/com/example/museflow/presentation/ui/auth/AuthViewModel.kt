package com.example.museflow.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.museflow.domain.usecase.LoginUseCase
import com.example.museflow.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun resetState() {
        _state.value = AuthState.Idle
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val token = loginUseCase(login, password)
                _state.value = AuthState.Success(token)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Ошибка входа")
            }
        }
    }

    fun register(login: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val token = registerUseCase(login, email, password)
                // Если сервер не возвращает токен при регистрации, делаем login
                if (token.isBlank()) {
                    val loginToken = loginUseCase(login, password)
                    _state.value = AuthState.Success(loginToken)
                } else {
                    _state.value = AuthState.Success(token)
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Ошибка регистрации")
            }
        }
    }
}