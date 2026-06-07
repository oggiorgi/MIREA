package com.example.museflow.utils

import java.util.Locale

/*
 * Утилиты для форматирования данных. 
 * Содержит логику преобразования технических единиц (секунды, числа) 
 * в человекочитаемый формат с учетом локализации и правил русского языка.
 */
object FormatUtils {
    fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, secs)
    }

    /*
     * Возвращает правильное склонение слова "трек" в зависимости от количества.
     */
    fun getTracksText(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> "трек"
            count % 10 in 2..4 && (count % 100 !in 10..20) -> "трека"
            else -> "треков"
        }
    }
}