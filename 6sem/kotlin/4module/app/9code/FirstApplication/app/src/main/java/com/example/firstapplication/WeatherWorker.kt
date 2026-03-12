package com.example.firstapplication

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import kotlin.random.Random

class WeatherWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val city = inputData.getString(KEY_CITY) ?: return Result.failure()
        Log.d("CityWorker", "Start loading $city")

        val delay = Random.nextLong(2000, 5000)
        Thread.sleep(delay)

        return if (Random.nextInt(100) < 10) {
            Log.e("CityWorker", "Failed to load $city")
            Result.failure()
        } else {
            val temperature = Random.nextInt(-10, 30)
            val output = Data.Builder()
                .putInt("temp_$city", temperature)
                .build()
            Log.d("CityWorker", "Loaded $city: $temperature°C")
            Result.success(output)
        }
    }

    companion object {
        const val KEY_CITY = "city"
    }
}