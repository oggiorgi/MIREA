package com.example.photocatalog.presentation.userdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photocatalog.domain.models.User
import com.example.photocatalog.domain.usecases.GetUserDetailUseCase
import com.example.photocatalog.domain.usecases.LogoutUseCase
import com.example.photocatalog.utils.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel для экрана деталей пользователя
class UserDetailViewModel(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val logoutUseCase: LogoutUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: Int = savedStateHandle["userId"] ?: -1

    private val _userState = MutableStateFlow<NetworkResult<User>>(NetworkResult.Loading)
    val userState: StateFlow<NetworkResult<User>> = _userState

    init {
        loadUserDetail()
    }

    fun loadUserDetail() {
        viewModelScope.launch {
            _userState.value = NetworkResult.Loading
            val result = getUserDetailUseCase(userId)
            _userState.value = if (result.isSuccess) {
                NetworkResult.Success(result.getOrNull() ?: return@launch)
            } else {
                NetworkResult.Error(result.exceptionOrNull()?.message ?: "Failed to load user")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}

// Factory для создания ViewModel
class UserDetailViewModelFactory(
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UserDetailViewModel(
            getUserDetailUseCase = getUserDetailUseCase,
            logoutUseCase = logoutUseCase,
            savedStateHandle = SavedStateHandle(mapOf("userId" to userId))
        ) as T
    }
}

// Экран деталей пользователя
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavController,
    userId: Int,
    getUserDetailUseCase: GetUserDetailUseCase,
    logoutUseCase: LogoutUseCase
) {
    val viewModel: UserDetailViewModel = viewModel(
        factory = UserDetailViewModelFactory(getUserDetailUseCase, logoutUseCase, userId)
    )

    val userState by viewModel.userState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали пользователя") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (userState) {
                is NetworkResult.Idle -> {
                    CircularProgressIndicator()
                }

                is NetworkResult.Loading -> {
                    CircularProgressIndicator()
                }

                is NetworkResult.Success -> {
                    val user = (userState as NetworkResult.Success<User>).data
                    UserDetailContent(
                        user = user,
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("users_list") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                is NetworkResult.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (userState as NetworkResult.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadUserDetail() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

// Содержимое экрана деталей
@Composable
fun UserDetailContent(
    user: User,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = user.image,
            contentDescription = "Аватар",
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${user.firstName} ${user.lastName}",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ID: ${user.id}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailContent() {
    val fakeUser = User(
        id = 1,
        firstName = "Emily",
        lastName = "Johnson",
        username = "emilyj",
        email = "emily.johnson@x.dummyjson.com",
        image = "https://dummyjson.com/icon/emilyj/128"
    )

    MaterialTheme {
        Surface {
            UserDetailContent(
                user = fakeUser,
                onLogout = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailError() {
    MaterialTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Не удалось загрузить пользователя",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { }) {
                        Text("Повторить")
                    }
                }
            }
        }
    }
}