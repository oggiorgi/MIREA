package com.example.heartrate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.heartrate.ui.theme.HeartrateTheme
import com.example.heartrate.presentation.BleScannerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeartrateTheme {
                BleScannerScreen()
            }
        }
    }
}