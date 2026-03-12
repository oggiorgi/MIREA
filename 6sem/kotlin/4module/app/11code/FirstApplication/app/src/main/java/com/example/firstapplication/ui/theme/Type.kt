package com.example.firstapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.firstapplication.R

val GoogleSansFlex = FontFamily(
    Font(R.font.google_sans_flex, FontWeight.Normal),
    Font(R.font.ravitasneegular, FontWeight.Normal)
)

val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = GoogleSansFlex),
    displayMedium = baseline.displayMedium.copy(fontFamily = GoogleSansFlex),
    displaySmall = baseline.displaySmall.copy(fontFamily = GoogleSansFlex),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = GoogleSansFlex),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = GoogleSansFlex),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = GoogleSansFlex),
    titleLarge = baseline.titleLarge.copy(fontFamily = GoogleSansFlex),
    titleMedium = baseline.titleMedium.copy(fontFamily = GoogleSansFlex),
    titleSmall = baseline.titleSmall.copy(fontFamily = GoogleSansFlex),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = GoogleSansFlex),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = GoogleSansFlex),
    bodySmall = baseline.bodySmall.copy(fontFamily = GoogleSansFlex),
    labelLarge = baseline.labelLarge.copy(fontFamily = GoogleSansFlex),
    labelMedium = baseline.labelMedium.copy(fontFamily = GoogleSansFlex),
    labelSmall = baseline.labelSmall.copy(fontFamily = GoogleSansFlex),
)
