package com.example.museflow.presentation.ui.catalog

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.museflow.domain.models.Playlist
import com.example.museflow.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Класс для управления историей поиска
class SearchHistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getHistory(): List<String> {
        val json = prefs.getString("history", "[]") ?: "[]"
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addQuery(query: String) {
        if (query.isBlank()) return
        val history = getHistory().toMutableList()
        history.remove(query)
        history.add(0, query)
        if (history.size > 10) history.removeAt(10)
        val json = gson.toJson(history)
        prefs.edit().putString("history", json).apply()
    }

    fun clearHistory() {
        prefs.edit().putString("history", "[]").apply()
    }
}

@Composable
fun CatalogScreen(
    onTrackClick: (Track) -> Unit,
    onGenreClick: (String) -> Unit,
    playlists: List<Playlist> = emptyList(),
    onAddToPlaylist: (Int, Int) -> Unit,
    viewModel: CatalogViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchHistoryManager = remember { SearchHistoryManager(context) }

    // Сохраняем поисковый запрос при повороте экрана
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var selectedTrackId by remember { mutableStateOf<Int?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    // История поиска
    val searchHistory = remember { searchHistoryManager.getHistory() }

    LaunchedEffect(selectedTrackId) {
        if (selectedTrackId != null) {
            showAddDialog = true
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Поле поиска с кнопкой очистки
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.search(it)
                if (it.isNotBlank()) {
                    searchHistoryManager.addQuery(it)
                    showHistory = false
                }
            },
            label = { Text("Поиск") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .onFocusChanged { focusState ->
                    isSearchFocused = focusState.isFocused
                    if (focusState.isFocused && searchQuery.isEmpty()) {
                        showHistory = searchHistory.isNotEmpty()
                    } else {
                        showHistory = false
                    }
                },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        viewModel.search("")
                        keyboardController?.hide()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистить")
                    }
                }
            }
        )

        // История поиска (показывается при фокусе на пустом поле)
        if (showHistory && searchHistory.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Недавние поиски",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = {
                            searchHistoryManager.clearHistory()
                            showHistory = false
                        }) {
                            Text("Очистить", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    searchHistory.forEach { query ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = query
                                    viewModel.search(query)
                                    keyboardController?.hide()
                                    showHistory = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = query)
                        }
                        Divider(modifier = Modifier.padding(start = 48.dp))
                    }
                }
            }
        }

        when (state) {
            is CatalogState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CatalogState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((state as CatalogState.Error).message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadTracks() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is CatalogState.Success -> {
                val tracks = (state as CatalogState.Success).tracks

                // Если есть поисковый запрос и результаты пусты
                if (searchQuery.isNotEmpty() && tracks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ничего не найдено по запросу \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = {
                                searchQuery = ""
                                viewModel.search("")
                                keyboardController?.hide()
                            }) {
                                Text("Очистить поиск")
                            }
                        }
                    }
                } else {
                    val tracksByGenre = tracks.groupBy { it.genre ?: "Другое" }
                    val dailyPlaylistTracks = tracks.shuffled().take(10)

                    LazyColumn {
                        if (dailyPlaylistTracks.isNotEmpty()) {
                            item {
                                DailyPlaylistSection(
                                    tracks = dailyPlaylistTracks,
                                    onTrackClick = onTrackClick,
                                    onAddToPlaylist = { trackId ->
                                        selectedTrackId = trackId
                                    }
                                )
                            }
                        }

                        tracksByGenre.forEach { (genre, genreTracks) ->
                            item {
                                GenreFolderSection(
                                    genreName = genre,
                                    tracks = genreTracks,
                                    onTrackClick = onTrackClick,
                                    onGenreClick = { onGenreClick(genre) },
                                    onAddToPlaylist = { trackId ->
                                        selectedTrackId = trackId
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && selectedTrackId != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = {
                showAddDialog = false
                selectedTrackId = null
            },
            onAdd = { playlistId ->
                onAddToPlaylist(playlistId, selectedTrackId!!)
                showAddDialog = false
                selectedTrackId = null
            }
        )
    }
}

// ==================== НОВАЯ СЕКЦИЯ ТРЕКОВ ДНЯ ================================================================================
@Composable
fun DailyPlaylistSection(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onAddToPlaylist: (Int) -> Unit
) {
    // Разбиваем треки на группы по 3
    val groupedTracks = tracks.chunked(3)
    val screenWidth = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp.dp

    if (groupedTracks.isEmpty()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "🎧 Треки дня",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Горизонтальная прокрутка для страниц
        val listState = rememberLazyListState()
        val currentPage by remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isNotEmpty()) {
                    visibleItems.first().index
                } else 0
            }
        }

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(0.dp), // Убираем отступы между страницами
            modifier = Modifier.fillMaxWidth()
        ) {
            items(groupedTracks) { trackGroup ->
                // Одна карточка на весь экран
                DailyPlaylistPageCard(
                    tracks = trackGroup,
                    onTrackClick = onTrackClick,
                    onAddToPlaylist = onAddToPlaylist,
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }

        // Индикатор страниц (точки) - теперь будет виден!
        if (groupedTracks.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                groupedTracks.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (currentPage == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun DailyPlaylistPageCard(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onAddToPlaylist: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = false) { },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            tracks.forEachIndexed { index, track ->
                DailyPlaylistPageItem(
                    track = track,
                    onTrackClick = onTrackClick,
                    onAddToPlaylist = onAddToPlaylist
                )

                if (index < tracks.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            repeat(3 - tracks.size) {
                Spacer(modifier = Modifier.height(70.dp))
            }
        }
    }
}

@Composable
fun DailyPlaylistPageItem(
    track: Track,
    onTrackClick: (Track) -> Unit,
    onAddToPlaylist: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrackClick(track) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = track.coverUrl),
            contentDescription = "Cover",
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.artist,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = { onAddToPlaylist(track.id) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add to playlist",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
// ==================== ОСТАЛЬНЫЕ КОМПОНЕНТЫ ====================================================================================================

@Composable
fun GenreFolderSection(
    genreName: String,
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onGenreClick: () -> Unit,
    onAddToPlaylist: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onGenreClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = genreName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = " (${tracks.size})",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(tracks.take(10)) { track ->
                GenreTrackCard(
                    track = track,
                    onClick = { onTrackClick(track) },
                    onAddToPlaylist = { onAddToPlaylist(track.id) }
                )
            }

            item {
                SeeAllButton(onClick = onGenreClick)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun GenreTrackCard(
    track: Track,
    onClick: () -> Unit,
    onAddToPlaylist: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface  // ← только это, без background
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Обложка
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = track.coverUrl,
                        error = painterResource(id = android.R.drawable.ic_menu_gallery)
                    ),
                    contentDescription = "Cover",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                        .padding(4.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),  // ← адаптивный
                    tonalElevation = 0.dp
                ) {
                    IconButton(
                        onClick = onAddToPlaylist,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add to playlist",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = track.title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = track.artist,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun SeeAllButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Folder,
                contentDescription = "See all",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Все",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "→",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}