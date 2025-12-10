package com.example.a2modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a2modul.ui.theme._2ModulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _2ModulTheme {
                // Задание 5: Серый контейнер с красным кругом и инициалами
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
){
    // Создание родительского контейнера
    Scaffold (
        // Установка Modifier с наслаиванием аттрибутов
        // те что были переданы при вызове MainScreen и записаны в переменную
                modifier = modifier
                .fillMaxSize(),
        // Заполнение контентом
        content = {
            Column(
                // Заполнение "чистого" Modifier
                modifier = Modifier
                    .padding(it)
                    .wrapContentSize(align = Alignment.TopStart),
                // Для Column базовой осью является вертикаль
                // Центровка вертикали с левого края будет выглядеть так:
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                CircleInTopRight()
            }
        }
    )
}

@Composable
fun CircleInTopRight() {
    Box(
        modifier = Modifier
            .size(width = 240.dp, height = 120.dp)
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.circle),
            contentDescription = "Красный круг",
            modifier = Modifier
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
fun FilledTonalButtonExample(onClick: () -> Unit){
    FilledTonalButton(onClick = {onClick() }) {
        Text("Tonal")
    }
}



@Preview(showBackground = true)
@Composable
fun Assignment5Preview() {
    _2ModulTheme {
        MainScreen()
    }
}



