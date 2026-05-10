package com.example.photocatalog.presentation.userslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.photocatalog.domain.usecases.GetUsersUseCase
import com.example.photocatalog.domain.usecases.LogoutUseCase

class UsersListViewModelFactory(
    private val getUsersUseCase: GetUsersUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UsersListViewModel(getUsersUseCase, logoutUseCase) as T
    }
}