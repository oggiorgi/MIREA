package com.example.firstapplication.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firstapplication.data.DiaryEntry
import com.example.firstapplication.viewmodel.DiaryViewModel

// Главный композабл приложения (навигация между экранами)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryApp() {
    val viewModel: DiaryViewModel = viewModel()
    val entries by viewModel.entries.collectAsState()
    val currentEntry by viewModel.currentEntry.collectAsState()

    var showEditor by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<DiaryEntry?>(null) }

    LaunchedEffect(currentEntry) {
        if (currentEntry != null && !showEditor) {
            showEditor = true
            editingEntry = currentEntry
        }
    }

    if (showEditor) {
        EntryEditorScreen(
            entry = editingEntry,
            onSave = { title, content ->
                viewModel.saveEntry(title, content, editingEntry?.filename)
                viewModel.clearCurrentEntry()
                showEditor = false
                editingEntry = null
            },
            onBack = {
                viewModel.clearCurrentEntry()
                showEditor = false
                editingEntry = null
            }
        )
    } else {
        DiaryListScreen(
            entries = entries,
            onEntryClick = { entry ->
                viewModel.selectEntry(entry)
            },
            onEntryLongClick = { entry ->
                entryToDelete = entry
                showDeleteDialog = true
            },
            onNewEntry = {
                editingEntry = null
                showEditor = true
            }
        )
    }

    if (showDeleteDialog && entryToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                entryToDelete = null
            },
            title = { Text("Удалить запись?") },
            text = { Text("Эта запись будет удалена безвозвратно") },
            confirmButton = {
                TextButton(
                    onClick = {
                        entryToDelete?.let { viewModel.deleteEntry(it.filename) }
                        showDeleteDialog = false
                        entryToDelete = null
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    entryToDelete = null
                }) {
                    Text("Отмена")
                }
            }
        )
    }
}

// Экран списка записей (LazyColumn с карточками)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiaryListScreen(
    entries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit,
    onEntryLongClick: (DiaryEntry) -> Unit,
    onNewEntry: () -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEntry) {
                Icon(Icons.Default.Add, contentDescription = "Новая запись")
            }
        }
    ) { paddingValues ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "У вас пока нет записей",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Нажмите + чтобы создать первую",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.filename }) { entry ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onEntryClick(entry) },
                                    onLongClick = { onEntryLongClick(entry) },
                                    indication = LocalIndication.current,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .padding(16.dp)
                        ) {
                            if (entry.title.isNotEmpty()) {
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = entry.getFormattedDate(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = entry.getPreview(),
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


// Экран создания/редактирования записи

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryEditorScreen(
    entry: DiaryEntry?,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(entry?.title ?: "") }
    var content by remember { mutableStateOf(entry?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entry == null) "Новая запись" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { onSave(title, content) }) {
                        Icon(Icons.Default.Save, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Заголовок (опционально)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Ваша запись...") },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                minLines = 10
            )
        }
    }
}