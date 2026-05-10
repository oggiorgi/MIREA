package com.example.photocatalog.presentation.userslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photocatalog.domain.models.User
import com.example.photocatalog.domain.usecases.GetUsersUseCase
import com.example.photocatalog.domain.usecases.LogoutUseCase
import com.example.photocatalog.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersListViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _usersState = MutableStateFlow<NetworkResult<List<User>>>(NetworkResult.Loading)
    val usersState: StateFlow<NetworkResult<List<User>>> = _usersState
    
    init {
        loadUsers()
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = NetworkResult.Loading
            val result = getUsersUseCase()
            _usersState.value = if (result.isSuccess) {
                NetworkResult.Success(result.getOrNull() ?: emptyList())
            } else {
                NetworkResult.Error(result.exceptionOrNull()?.message ?: "Failed to load users")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun clearState() {
        _usersState.value = NetworkResult.Loading
    }
}