package com.example.firstapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.firstapplication.R

// Ravitas Neegular - только для заголовков
val RavitasNeegular = FontFamily(
    Font(R.font.ravitasneegular, FontWeight.Normal),
    Font(R.font.ravitasneegular, FontWeight.Bold)
)


val AppTypography = Typography().run {
    copy(
        // Только заголовки - Ravitas
        displayLarge = displayLarge.copy(fontFamily = RavitasNeegular),
        displayMedium = displayMedium.copy(fontFamily = RavitasNeegular),
        displaySmall = displaySmall.copy(fontFamily = RavitasNeegular),
        headlineLarge = headlineLarge.copy(fontFamily = RavitasNeegular),
        headlineMedium = headlineMedium.copy(fontFamily = RavitasNeegular),
        headlineSmall = headlineSmall.copy(fontFamily = RavitasNeegular),
        titleLarge = titleLarge.copy(fontFamily = RavitasNeegular),
        titleMedium = titleMedium.copy(fontFamily = RavitasNeegular),
        titleSmall = titleSmall.copy(fontFamily = RavitasNeegular),

        // Основной текст - стандартный системный шрифт (убираем fontFamily)
        bodyLarge = bodyLarge.copy(fontFamily = null),
        bodyMedium = bodyMedium.copy(fontFamily = null),
        bodySmall = bodySmall.copy(fontFamily = null),
        labelLarge = labelLarge.copy(fontFamily = null),
        labelMedium = labelMedium.copy(fontFamily = null),
        labelSmall = labelSmall.copy(fontFamily = null)
    )
}