package com.example.firstapplication.ui.theme


import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import com.example.firstapplication.R


val GoogleSansFlex = FontFamily(
    Font(R.font.flex, FontWeight.Normal),
    Font(R.font.gular, FontWeight.Normal)
)

val AppTypography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = GoogleSansFlex),
        displayMedium = displayMedium.copy(fontFamily = GoogleSansFlex),
        displaySmall = displaySmall.copy(fontFamily = GoogleSansFlex),
        headlineLarge = headlineLarge.copy(fontFamily = GoogleSansFlex),
        headlineMedium = headlineMedium.copy(fontFamily = GoogleSansFlex),
        headlineSmall = headlineSmall.copy(fontFamily = GoogleSansFlex),
        titleLarge = titleLarge.copy(fontFamily = GoogleSansFlex),
        titleMedium = titleMedium.copy(fontFamily = GoogleSansFlex),
        bodyLarge = bodyLarge.copy(fontFamily = GoogleSansFlex)
    )
}