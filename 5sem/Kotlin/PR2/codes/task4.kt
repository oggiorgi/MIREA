package com.example.a2modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a2modul.ui.theme._2ModulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _2ModulTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Задание 1
                        Hello(
                            name = "Анна",
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Задание 3 - вариант 1
                        TextVariant1()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Задание 3 - вариант 2
                        TextVariant2()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Задание 3 - вариант 3
                        TextVariant3()

                        Spacer(modifier = Modifier.height(32.dp))

                        // Задание 4: Кастомная кнопка
                        CustomButton()
                    }
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

// ЗАДАНИЕ 3: Вариант 1
@Composable
fun TextVariant1() {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.compose_description),
                color = Color.Green,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ЗАДАНИЕ 3: Вариант 2
@Composable
fun TextVariant2() {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.compose_description),
                color = Color.Black,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ЗАДАНИЕ 3: Вариант 3
@Composable
fun TextVariant3() {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Green)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.compose_description),
                fontSize = 24.sp,
                color = Color.Black,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(start = 48.dp, top = 16.dp, bottom = 16.dp)
            )
        }
    }
}

// Задание 4: Кастомная кнопка
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomButton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Gray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        onClick = {
            // Действие при нажатии кнопки
        }
    ) {
        Text(
            text = "Нажми на меня",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

// ЗАДАНИЕ 2: Preview
@Preview(
    device = Devices.PIXEL_4,
    showSystemUi = true,
    name = "Portrait Preview"
)
@Composable
fun HelloPreviewPortrait() {
    _2ModulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Hello(name = "Иван (Portrait)")
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant1()
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant2()
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant3()
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton()
        }
    }
}

@Preview(
    device = Devices.PIXEL_4,
    showSystemUi = true,
    name = "Landscape Preview"
)
@Composable
fun HelloPreviewLandscape() {
    _2ModulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Hello(name = "Мария (Landscape)")
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant1()
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant2()
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant3()
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton()
        }
    }
}



// Preview для задания 3
@Preview(showBackground = true)
@Composable
fun TextVariantsPreview() {
    _2ModulTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TextVariant1()
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant2()
            Spacer(modifier = Modifier.height(16.dp))
            TextVariant3()
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton()
        }
    }
}
