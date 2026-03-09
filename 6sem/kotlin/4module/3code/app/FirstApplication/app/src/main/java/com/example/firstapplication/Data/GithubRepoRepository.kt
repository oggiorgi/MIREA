package com.example.firstapplication.Data


import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.IOException
import com.example.firstapplication.R

class GithubRepoRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }
    private var allRepos: List<Repo> = emptyList()

    // Загружаем список репозиториев из raw-ресурса (вызывается один раз)
    suspend fun loadRepos(): List<Repo> = withContext(Dispatchers.IO) {
        if (allRepos.isNotEmpty()) return@withContext allRepos
        val inputStream = context.resources.openRawResource(R.raw.github_repos)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        allRepos = try {
            json.decodeFromString<List<Repo>>(jsonString)
        } catch (e: Exception) {
            throw IOException("Ошибка парсинга JSON", e)
        }
        allRepos
    }

    // Поиск по full_name и description (регистронезависимый)
    suspend fun search(query: String): List<Repo> {
        // Имитация задержки сети (для демонстрации debounce)
        delay(500)
        if (query.isBlank()) return emptyList()
        val lowerQuery = query.lowercase()
        return allRepos.filter { repo ->
            repo.full_name.lowercase().contains(lowerQuery) ||
                    (repo.description?.lowercase()?.contains(lowerQuery) == true)
        }
    }
}