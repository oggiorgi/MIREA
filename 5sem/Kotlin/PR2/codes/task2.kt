package com.example.a2modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a2modul.ui.theme._2ModulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _2ModulTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Hello(
                        name = "Анна",
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun Hello(name: String?, modifier: Modifier = Modifier) {
    Surface(
        color = Color.Yellow,
        modifier = modifier
    ) {
        Text(
            text = if (name != null) "Привет, $name!" else "Имя не задано",
            modifier = Modifier.padding(16.dp)
        )
    }
}

// ЗАДАНИЕ 2: Три макета предпросмотра

// 1. Portrait (портретная ориентация)
@Preview(
    device = Devices.PIXEL_4,
    showSystemUi = true,
    name = "Portrait Preview"
)
@Composable
fun HelloPreviewPortrait() {
    _2ModulTheme {
        Hello(name = "Иван (Portrait)")
    }
}

// 2. Landscape (альбомная ориентация)
@Preview(
    device = Devices.PIXEL_4,
    showSystemUi = true,
    name = "Landscape Preview"
)
@Composable
fun HelloPreviewLandscape() {
    _2ModulTheme {
        Hello(name = "Мария (Landscape)")
    }
}

// 3. Round (круглый дисплей 200dp x 200dp с желтым фоном)
@Preview(
    widthDp = 200,
    heightDp = 200,
    name = "Round 200dp Preview"
)
@Composable
fun HelloPreviewRound() {
    // Желтый задний фон
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.Yellow),
        contentAlignment = Alignment.Center
    ) {
        // Сама функция Hello
        Hello(
            name = "Алексей (Round)",
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun HelloPreviewWearRound() {
    _2ModulTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)
        ) {
            Hello(name = "Олег (Wear)")
        }
    }
}
