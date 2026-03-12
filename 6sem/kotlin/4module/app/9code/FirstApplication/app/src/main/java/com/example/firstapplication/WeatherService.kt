package com.example.firstapplication

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asFlow
import androidx.work.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class WeatherService : Service() {

    private lateinit var workManager: WorkManager
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val uniqueWorkName = "weather_fetch_work"

    override fun onCreate() {
        super.onCreate()
        workManager = WorkManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundWithType()
        startWorkChain()
        observeWorkUpdates()
        return START_NOT_STICKY
    }

    private fun startForegroundWithType() {
        val notification = createNotification("Запуск...")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                App.NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(App.NOTIFICATION_ID, notification)
        }
    }

    private fun startWorkChain() {
        val cities = listOf("Москва", "Лондон", "Нью-Йорк")

        val cityRequests = cities.map { city ->
            OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInputData(workDataOf(WeatherWorker.KEY_CITY to city))
                .addTag("city_$city")
                .build()
        }

        val aggregateRequest = OneTimeWorkRequestBuilder<AggregateWorker>()
            .addTag("aggregate")
            .build()

        workManager
            .beginUniqueWork(
                uniqueWorkName,
                ExistingWorkPolicy.REPLACE,
                cityRequests
            )
            .then(aggregateRequest)
            .enqueue()
    }

    private fun observeWorkUpdates() {
        val workInfosLiveData = workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)

        serviceScope.launch {
            workInfosLiveData.asFlow().collectLatest { workInfos ->
                val notificationText = buildNotificationText(workInfos)
                updateNotification(notificationText)

                if (workInfos.all { it.state.isFinished }) {
                    stopSelf()
                }
            }
        }
    }

    private fun buildNotificationText(workInfos: List<WorkInfo>): String {
        val cityStates = mutableMapOf<String, String>()
        var aggregateDone = false

        for (info in workInfos) {
            val tags = info.tags
            when {
                tags.any { it.startsWith("city_") } -> {
                    val city = tags.first { it.startsWith("city_") }.removePrefix("city_")
                    val state = when (info.state) {
                        WorkInfo.State.ENQUEUED -> "ожидание"
                        WorkInfo.State.RUNNING -> "загрузка..."
                        WorkInfo.State.SUCCEEDED -> {
                            val temp = info.outputData.getInt("temp_$city", 0)
                            "$temp°C"
                        }
                        WorkInfo.State.FAILED -> "ошибка"
                        else -> "?"
                    }
                    cityStates[city] = state
                }
                tags.contains("aggregate") -> {
                    aggregateDone = info.state == WorkInfo.State.SUCCEEDED
                }
            }
        }

        val readyCities = cityStates.filterValues { it.contains("°C") }.keys
        val pendingCities = cityStates.filterValues { it == "загрузка..." || it == "ожидание" }.keys

        return when {
            aggregateDone -> {
                val report = workInfos.firstOrNull { it.tags.contains("aggregate") }
                    ?.outputData?.getString("report") ?: "Готово"
                report
            }
            readyCities.size == cityStates.size -> {
                "Все данные получены, формируем отчёт..."
            }
            else -> {
                val readyStr = if (readyCities.isNotEmpty()) readyCities.joinToString(" и ") else "нет"
                val pendingStr = if (pendingCities.isNotEmpty()) pendingCities.joinToString(", ") else "все готовы"
                "Готово: $readyStr, $pendingStr в процессе..."
            }
        }
    }

    private fun createNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Сбор прогноза погоды")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val notification = createNotification(text)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(App.NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}