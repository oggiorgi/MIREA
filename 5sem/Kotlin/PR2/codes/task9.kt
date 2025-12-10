package com.example.a2modul

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.a2modul.ui.theme._2ModulTheme
import kotlin.random.Random

data class Cart(
    val products: List<Product>
)

data class Product(
    val id: Int,
    val name: String,
    val price: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            _2ModulTheme {
                Scaffold { padding ->
                    ShoppingCartScreen(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    @Composable
    fun ShoppingCartScreen(modifier: Modifier = Modifier) {
        var products by remember {
            mutableStateOf(
                listOf(
                    Product(0, "Товар #1", 100),
                    Product(1, "Товар #2", 150),
                    Product(2, "Товар #3", 56)
                )
            )
        }

        // Вычисляем сумму динамически
        val totalSum = products.sumOf { it.price }
        val productSize = products.size

        Column(
            modifier = modifier.padding(16.dp)
        ) {
            for (product in products) {
                Text(text = "${product.name} - ${product.price} рублей")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Товаров на сумму: $totalSum рублей")

            AddProductSection(
                totalSum = totalSum,
                onAdd = {
                    products = products + Product(
                        id = products.size,
                        name = "Товар #${products.size + 1}",
                        price = Random.nextInt(0, 100)
                    )
                }
            )

            RemoveProductSection(
                productSize = productSize,
                onRemove = {
                    products = products.dropLast(1)
                }
            )
        }
    }

    @Composable
    fun AddProductSection(
        totalSum: Int,
        onAdd: () -> Unit
    ) {
        val context = LocalContext.current
        var previousSum by remember { mutableStateOf(totalSum) }

        // Показываем Toast только когда сумма переходит через 500
        LaunchedEffect(totalSum) {
            if (totalSum > 500 && previousSum <= 500) {
                Toast.makeText(context, "Доставка бесплатная!", Toast.LENGTH_SHORT).show()
            }
            previousSum = totalSum
        }

        Button(onClick = onAdd) {
            Text(text = "Добавить товар")
        }
    }

    @Composable
    fun RemoveProductSection(
        productSize: Int,
        onRemove: () -> Unit
    ) {
        if (productSize > 0) {
            Button(onClick = onRemove) {
                Text(text = "Удалить товар")
            }
        }
    }
}
