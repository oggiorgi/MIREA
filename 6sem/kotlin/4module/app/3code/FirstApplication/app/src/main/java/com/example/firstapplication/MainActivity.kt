package com.example.firstapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.firstapplication.Data.GithubRepoRepository
import com.example.firstapplication.Data.Repo
import kotlinx.coroutines.*
import androidx.compose.ui.text.font.FontWeight
import com.example.firstapplication.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private lateinit var repository: GithubRepoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = GithubRepoRepository(this)

        setContent {
            AppTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ){
                    SearchScreen(repository)
                }
            }
        }
    }
}

@Composable
fun SearchScreen(repository: GithubRepoRepository) {

    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Repo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Загружаем все репозитории при первом запуске (один раз)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            repository.loadRepos()
        }
    }

    // Debounce-эффект: при изменении searchQuery запускаем поиск с задержкой
    LaunchedEffect(searchQuery) {
        // Отменяем предыдущий поиск, если он ещё выполняется
        // (новый запуск автоматически отменит предыдущую корутину LaunchedEffect)
        if (searchQuery.isBlank()) {
            searchResults = emptyList()
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        errorMessage = null

        // Задержка 500 мс (debounce)
        delay(500)

        // Выполняем поиск в фоновом потоке
        val result = try {
            withContext(Dispatchers.IO) {
                repository.search(searchQuery)
            }
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки: ${e.message}"
            emptyList()
        }

        // Обновляем результаты и снимаем индикатор загрузки
        searchResults = result
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Поле ввода
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Поиск репозиториев...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Индикатор загрузки или сообщение об ошибке
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Список результатов
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { repo ->
                RepoItem(repo)
            }
        }
    }
}

@Composable
fun RepoItem(repo: Repo) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = repo.full_name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            repo.description?.let {
                Text(text = it, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "⭐ ${repo.stargazers_count}", fontSize = 12.sp)
                repo.language?.let { lang ->
                    Text(text = "🔹 $lang", fontSize = 12.sp)
                }
            }
        }
    }
}