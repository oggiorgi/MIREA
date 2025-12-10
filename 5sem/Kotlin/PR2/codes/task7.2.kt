package com.example.a2modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.a2modul.ui.theme._2ModulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _2ModulTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { padding ->
        ContactRowCard(
            contact = sampleContact,
            background = Color(0xFFE0E0E0),
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 12.dp)
        )
    }
}
A
@Composable
fun ContactRowCard(
    contact: Contact,
    background: Color = Color(0xFFE0E0E0),
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Имя: ${contact.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text("Отчество: ${contact.patronymic}", style = MaterialTheme.typography.bodyMedium)
            Text("Фамилия: ${contact.surname}", style = MaterialTheme.typography.bodyMedium)
            Text("Мобильный телефон: ${contact.mobile}", style = MaterialTheme.typography.bodyMedium)
            Text("Адрес: ${contact.address}", style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Избранное",
            tint = Color(0xFFFF9800)
        )
    }
}

data class Contact(
    val name: String,
    val patronymic: String,
    val surname: String,
    val mobile: String,
    val address: String
)

private val sampleContact = Contact(
    name = "Евгений",
    patronymic = "Андреевич",
    surname = "Ларин",
    mobile = "+7 495 495 95 95",
    address = "г. Москва, 3-я улица Строителей, д. 25, кв. 12"
)

@Preview(showBackground = true, name = "RowCardPreview")
@Composable
fun RowCardPreview() {
    _2ModulTheme {
        ContactRowCard(contact = sampleContact, background = Color(0xFFE0E0E0))
    }
}
