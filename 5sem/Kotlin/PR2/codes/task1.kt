package com.example.a2modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a2modul.ui.theme._2ModulTheme
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Вариант 1: с именем
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

// ЗАДАНИЕ 1: Простая функция Hello
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

// Preview для проверки
@Preview(showBackground = true)
@Composable
fun HelloPreview() {
    _2ModulTheme {
        Hello(name = "Иван")
    }
}

@Preview(showBackground = true)
@Composable
fun HelloPreviewNull() {
    _2ModulTheme {
        Hello(name = null)
    }
