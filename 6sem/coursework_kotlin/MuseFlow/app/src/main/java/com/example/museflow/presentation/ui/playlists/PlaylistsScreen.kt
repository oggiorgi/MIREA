package com.example.museflow.presentation.ui.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.models.Track

@Composable
fun PlaylistsScreen(
    onPlaylistClick: (Playlist) -> Unit,
    viewModel: PlaylistsViewModel
) {
    val state by viewModel.state.collectAsState()
    val isCreating by viewModel.isCreating.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (!isCreating) showCreateDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Создать плейлист")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is PlaylistsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PlaylistsState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text((state as PlaylistsState.Error).message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadPlaylists() }) {
                            Text("Повторить")
                        }
                    }
                }
                is PlaylistsState.Success -> {
                    val playlists = (state as PlaylistsState.Success).playlists
                    if (playlists.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Нет плейлистов")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { showCreateDialog = true }) {
                                Text("Создать первый плейлист")
                            }
                        }
                    } else {
                        LazyColumn {
                            items(playlists) { playlist ->
                                PlaylistItem(
                                    playlist = playlist,
                                    onClick = { onPlaylistClick(playlist) },
                                    onDelete = { viewModel.deletePlaylist(playlist.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${playlist.tracks.size} треков",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создать плейлист") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название плейлиста") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

// ==================== PREVIEWS ====================

// Preview для элемента плейлиста
@Preview(showBackground = true, name = "Playlist Item Preview")
@Composable
fun PlaylistItemPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            PlaylistItem(
                playlist = Playlist(
                    id = 1,
                    name = "Мои любимые треки",
                    coverUrl = null,
                    tracks = List(5) { index ->
                        Track(
                            id = index,
                            title = "Трек $index",
                            artist = "Исполнитель $index",
                            duration = 180 + index * 10,
                            coverUrl = "",
                            audioUrl = "",
                            genre = "Rock"
                        )
                    }
                ),
                onClick = {},
                onDelete = {}
            )
        }
    }
}

// Preview для списка плейлистов (Success состояние)
@Preview(showBackground = true, name = "Playlists Screen Success Preview")
@Composable
fun PlaylistsScreenSuccessPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val samplePlaylists = listOf(
                Playlist(
                    id = 1,
                    name = "Rock Classics",
                    coverUrl = null,
                    tracks = List(8) { index ->
                        Track(
                            id = index,
                            title = "Rock Track $index",
                            artist = "Rock Band",
                            duration = 200,
                            coverUrl = "",
                            audioUrl = "",
                            genre = "Rock"
                        )
                    }
                ),
                Playlist(
                    id = 2,
                    name = "Pop Hits",
                    coverUrl = null,
                    tracks = List(12) { index ->
                        Track(
                            id = index,
                            title = "Pop Track $index",
                            artist = "Pop Star",
                            duration = 210,
                            coverUrl = "",
                            audioUrl = "",
                            genre = "Pop"
                        )
                    }
                ),
                Playlist(
                    id = 3,
                    name = "Шансон",
                    coverUrl = null,
                    tracks = List(3) { index ->
                        Track(
                            id = index,
                            title = "Шарик",
                            artist = "Бутырка",
                            duration = 180,
                            coverUrl = "",
                            audioUrl = "",
                            genre = "Шансон"
                        )
                    }
                )
            )

            LazyColumn {
                items(samplePlaylists) { playlist ->
                    PlaylistItem(
                        playlist = playlist,
                        onClick = {},
                        onDelete = {}
                    )
                }
            }
        }
    }
}

// Preview для пустого состояния
@Preview(showBackground = true, name = "Empty Playlists Preview")
@Composable
fun EmptyPlaylistsPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Нет плейлистов")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {}) {
                        Text("Создать первый плейлист")
                    }
                }
            }
        }
    }
}

// Preview для состояния загрузки
@Preview(showBackground = true, name = "Loading State Preview")
@Composable
fun PlaylistsLoadingPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// Preview для диалога создания плейлиста
@Preview(showBackground = true, name = "Create Playlist Dialog Preview")
@Composable
fun CreatePlaylistDialogPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CreatePlaylistDialog(
                onDismiss = {},
                onCreate = {}
            )
        }
    }
}