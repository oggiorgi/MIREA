package com.example.photocatalog.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.photocatalog.presentation.viewmodel.LaureateViewModel
import com.example.photocatalog.presentation.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaureateListScreen(
    viewModel: LaureateViewModel,
    onNavigateToDetail: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedYear by remember { mutableStateOf("2010") }
    var selectedCategory by remember { mutableStateOf("Physics") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Нобелевские премии",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Панель фильтров
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Год
                    OutlinedTextField(
                        value = selectedYear,
                        onValueChange = { selectedYear = it },
                        label = { Text("Год") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Категория
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Категория") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("Physics", "Chemistry", "Physiology or Medicine", "Literature", "Peace", "Economic Sciences").forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Кнопка Фильтр
                    Button(
                        onClick = {
                            viewModel.filterByYear(selectedYear)
                            viewModel.filterByCategory(
                                when(selectedCategory) {
                                    "Physics" -> "physics"
                                    "Chemistry" -> "chemistry"
                                    "Physiology or Medicine" -> "physiology or medicine"
                                    "Literature" -> "literature"
                                    "Peace" -> "peace"
                                    "Economic Sciences" -> "economic sciences"
                                    else -> selectedCategory.lowercase()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Фильтр")
                    }
                }
            }

            // Список премий
            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is UiState.Success -> {
                        val grouped = state.laureates
                            .groupBy { it.year }
                            .toSortedMap(compareByDescending { it })

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            grouped.forEach { (year, laureatesInYear) ->
                                val byCategory = laureatesInYear.groupBy { it.category }

                                item {
                                    Text(
                                        text = year,
                                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                }

                                byCategory.forEach { (category, laureates) ->
                                    item {
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {onNavigateToDetail(year, category)},
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text(
                                                    text = "$year – ${getCategoryDisplayName(category)}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = laureates.joinToString(", ") { it.name },
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = laureates.first().motivation,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.message)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadLaureates() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryDisplayName(category: String): String {
    return when (category.lowercase()) {
        "physics" -> "PHYSICS"
        "chemistry" -> "CHEMISTRY"
        "physiology or medicine" -> "PHYSIOLOGY OR MEDICINE"
        "literature" -> "LITERATURE"
        "peace" -> "PEACE"
        "economic sciences" -> "ECONOMIC SCIENCES"
        else -> category.uppercase()
    }
}