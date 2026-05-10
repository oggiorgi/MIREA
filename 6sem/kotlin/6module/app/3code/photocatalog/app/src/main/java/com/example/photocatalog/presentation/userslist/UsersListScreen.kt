package com.example.photocatalog.presentation.userslist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photocatalog.domain.models.User
import com.example.photocatalog.utils.NetworkResult
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.photocatalog.di.AppModule
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    navController: NavController,
    viewModel: UsersListViewModel = viewModel()
) {
    val usersState by viewModel.usersState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователи") },
                actions = {
                    // Кнопка выхода - очищает токен и возвращает на экран логина
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.logout()
                                viewModel.clearState()
                                navController.navigate("login") {
                                    popUpTo("users_list") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Выйти"
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
            when (usersState) {
                is NetworkResult.Idle,
                is NetworkResult.Loading -> {
                    CircularProgressIndicator()
                }

                is NetworkResult.Success -> {
                    UserListContent(
                        users = (usersState as NetworkResult.Success<List<User>>).data,
                        onUserClick = { user ->
                            navController.navigate("user_detail/${user.id}")
                        }
                    )
                }

                is NetworkResult.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (usersState as NetworkResult.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadUsers() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserListContent(
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            UserCard(user = user, onClick = { onUserClick(user) })
        }
    }
}

@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = user.image,
                contentDescription = "Avatar",
                modifier = Modifier.size(64.dp)
            )

            Column {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// --- Previews ---

@Preview(showBackground = true)
@Composable
fun PreviewUserListContent() {
    val fakeUsers = listOf(
        User(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            username = "johnd",
            email = "john.doe@example.com",
            image = "https://dummyjson.com/icon/johnd/128"
        ),
        User(
            id = 2,
            firstName = "Emily",
            lastName = "Smith",
            username = "emilys",
            email = "emily.smith@example.com",
            image = "https://dummyjson.com/icon/emilys/128"
        ),
        User(
            id = 3,
            firstName = "Michael",
            lastName = "Johnson",
            username = "michaelw",
            email = "michael.j@example.com",
            image = "https://dummyjson.com/icon/michaelw/128"
        )
    )

    MaterialTheme {
        Surface {
            UserListContent(users = fakeUsers, onUserClick = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListLoading() {
    MaterialTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserListError() {
    MaterialTheme {
        Surface {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Failed to load users",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}