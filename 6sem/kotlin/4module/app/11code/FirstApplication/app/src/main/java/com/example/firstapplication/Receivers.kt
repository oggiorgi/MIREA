package com.example.firstapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("ReminderReceiver", "========= НАПОМИНАНИЕ СРАБОТАЛО =========")

        // Показываем уведомление
        NotificationHelper.showNotification(
            context,
            "Время принять таблетку!",
            "Не забудьте принять таблетку в 20:00"
        )

        // Планируем на завтра
        ReminderManager.scheduleReminder(context)
    }
}