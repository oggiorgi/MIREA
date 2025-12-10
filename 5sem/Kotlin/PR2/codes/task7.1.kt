package com.example.a2modul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        ContactListScreen(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
fun ContactListScreen(modifier: Modifier = Modifier) {
    val contacts = listOf(
        Contact(
            name = "Евгений",
            patronymic = "Андреевич",
            surname = "Ларин",
            mobile = "+7 495 495 95 95",
            address = "г. Москва, 3-я улица Строителей, д. 25, кв. 12"
        ),
        Contact(
            name = "Василий",
            patronymic = "Гаврилович",
            surname = "Трофимов",
            mobile = "+7 965 778 78 78",
            address = "Московская область, д. Крутово, д. 4"
        ),
        Contact(
            name = "Людмила",
            patronymic = "Петровна",
            surname = "Полякова",
            mobile = "+7 495 495 95 95",
            address = "г. Москва, 3-я улица Строителей, д. 25, кв. 12"
        ),
        Contact(
            name = "Кирилл",
            patronymic = "Андреевич",
            surname = "Капустин",
            mobile = "+7 965 778 78 78",
            address = "г. Москва, Большая Никитская, д. 43, кв. 290"
        )
    )

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        contacts.forEachIndexed { index, contact ->
            val background = if (index == 0) Color.Transparent else Color(0xFFE0E0E0)
            ContactCard(contact = contact, background = background)
        }
    }
}

@Composable
fun ContactCard(
    contact: Contact,
    background: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.Start
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
}

data class Contact(
    val name: String,
    val patronymic: String,
    val surname: String,
    val mobile: String,
    val address: String
)

@Preview(showBackground = true, name = "ContactColumnPreview")
@Composable
fun ContactColumnPreview() {
    _2ModulTheme {
        ContactCard(
            contact = Contact(
                name = "Евгений",
                patronymic = "Андреевич",
                surname = "Ларин",
                mobile = "+7 495 495 95 95",
                address = "г. Москва, 3-я улица Строителей, д. 25, кв. 12"
            ),
            background = Color.Transparent
        )
    }
}

@Preview(showBackground = true, name = "ListPreview")
@Composable
fun ListPreview() {
    _2ModulTheme {
        ContactListScreen()
    }
}
