package com.example.firstapplication

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.asFlow
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.firstapplication.ui.theme.AppTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = true) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherReportScreen(
                        onStartClick = { startWeatherFetch() }
                    )
                }
            }
        }
    }

    private fun startWeatherFetch() {
        val intent = Intent(this, WeatherService::class.java)
        startForegroundService(intent)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        isServiceBound = true
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}

@Composable
fun WeatherReportScreen(onStartClick: () -> Unit) {
    val context = LocalContext.current
    val cities = listOf("Москва", "Лондон", "Нью-Йорк")
    val workManager = WorkManager.getInstance(context)
    val scope = rememberCoroutineScope()

    val cityStates = remember { mutableStateMapOf<String, CityState>() }
    var finalReport by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Инициализация пустого состояния при запуске
    LaunchedEffect(Unit) {
        clearState(cities, cityStates, { finalReport = null }, { isLoading = false })
    }

    // Очистка состояния при выходе из приложения
    DisposableEffect(Unit) {
        onDispose {
            scope.launch {
                // Не очищаем WorkManager данные, только UI состояние
            }
        }
    }

    LaunchedEffect(workManager) {
        workManager.getWorkInfosForUniqueWorkLiveData("weather_fetch_work")
            .asFlow()
            .map { workInfos ->
                // Фильтруем только активные или завершенные в этой сессии работы
                val hasActiveWork = workInfos.any {
                    it.state == WorkInfo.State.RUNNING ||
                            it.state == WorkInfo.State.ENQUEUED
                }

                // Если нет активных работ, проверяем, были ли они завершены недавно
                val recentWork = if (!hasActiveWork) {
                    // Возвращаем пустой список, чтобы очистить UI
                    emptyList()
                } else {
                    workInfos
                }

                recentWork to hasActiveWork
            }
            .distinctUntilChanged()
            .collect { (workInfos, hasActiveWork) ->
                isLoading = hasActiveWork

                if (workInfos.isEmpty()) {
                    // Если нет активных работ, очищаем состояние
                    clearState(cities, cityStates, { finalReport = null }, {})
                } else {
                    updateCityStates(workInfos, cityStates)
                    finalReport = extractFinalReport(workInfos)
                }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Прогноз погоды", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cities) { city ->
                val state = cityStates[city] ?: CityState(city, "Ожидание")
                CityRow(cityState = state)
            }
        }

        if (!finalReport.isNullOrBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = finalReport!!,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Очищаем состояние перед новым запуском
                clearState(cities, cityStates, { finalReport = null }, { isLoading = true })
                onStartClick()
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Загрузка..." else "Собрать прогноз")
        }
    }
}

// Функция для очистки состояния
private fun clearState(
    cities: List<String>,
    cityStates: MutableMap<String, CityState>,
    clearFinalReport: () -> Unit,
    setLoadingState: () -> Unit
) {
    cities.forEach { city ->
        cityStates[city] = CityState(city = city, status = "Ожидание")
    }
    clearFinalReport()
    setLoadingState()
}

@Composable
fun CityRow(cityState: CityState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(cityState.city, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = when {
                cityState.status == "Готово" -> "${cityState.temperature ?: ""}°C"
                else -> cityState.status
            },
            style = MaterialTheme.typography.bodyLarge,
            color = when (cityState.status) {
                "Готово" -> MaterialTheme.colorScheme.primary
                "Ошибка" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            }
        )
    }
}

private fun updateCityStates(
    workInfos: List<WorkInfo>,
    cityStates: MutableMap<String, CityState>
) {
    for (info in workInfos) {
        val cityTag = info.tags.firstOrNull { it.startsWith("city_") } ?: continue
        val city = cityTag.removePrefix("city_")
        val state = when (info.state) {
            WorkInfo.State.ENQUEUED -> "Ожидание"
            WorkInfo.State.RUNNING -> "Загрузка..."
            WorkInfo.State.SUCCEEDED -> "Готово"
            WorkInfo.State.FAILED -> "Ошибка"
            else -> "?"
        }
        val temp = info.outputData.getInt("temp_$city", 0)
        cityStates[city] = CityState(
            city = city,
            status = state,
            temperature = if (state == "Готово") temp else null
        )
    }
}

private fun extractFinalReport(workInfos: List<WorkInfo>): String? {
    val aggregateInfo = workInfos.firstOrNull { it.tags.contains("aggregate") }
    return if (aggregateInfo?.state == WorkInfo.State.SUCCEEDED) {
        aggregateInfo.outputData.getString("report")
    } else {
        null
    }
}

data class CityState(
    val city: String,
    val status: String,
    val temperature: Int? = null
)