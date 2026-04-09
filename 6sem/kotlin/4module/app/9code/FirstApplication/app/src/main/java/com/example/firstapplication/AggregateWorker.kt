package com.example.firstapplication

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class AggregateWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("AggregateWorker", "Aggregating results")

        val cityTemps = mutableMapOf<String, Int>()
        val allKeys = inputData.keyValueMap.keys

        for (key in allKeys) {
            if (key.startsWith("temp_")) {
                val city = key.removePrefix("temp_")
                val temp = inputData.getInt(key, Int.MIN_VALUE)
                if (temp != Int.MIN_VALUE) {
                    cityTemps[city] = temp
                }
            }
        }

        if (cityTemps.isEmpty()) {
            return Result.failure(workDataOf("error" to "Нет данных о городах"))
        }

        val average = cityTemps.values.average().toInt()
        val report = buildString {
            appendLine("Итоговый прогноз:")
            cityTemps.forEach { (city, temp) ->
                val weather = if (temp < 0) "дождь" else "ясно"
                appendLine("$city: $temp°C, $weather")
            }
            append("Средняя температура: $average°C")
        }

        return Result.success(workDataOf("report" to report))
    }
}