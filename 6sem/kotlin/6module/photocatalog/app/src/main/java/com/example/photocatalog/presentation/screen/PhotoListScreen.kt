package com.example.photocatalog.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.annotation.ExperimentalCoilApi
import com.example.photocatalog.domain.entity.Photo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun PhotoListScreen(
    navController: NavController,
    viewModel: PhotoListViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фотокаталог") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is PhotoListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is PhotoListState.Success -> {
                    val photos = (state as PhotoListState.Success).photos

                    // Выводим URL для отладки
                    LaunchedEffect(photos) {
                        photos.forEach { photo ->
                            println("📸 Photo URL: ${photo.downloadUrl}")
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        items(photos) { photo ->
                            PhotoItem(photo = photo) {
                                navController.navigate("detail/${photo.id}")
                            }
                        }
                    }
                }
                is PhotoListState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (state as PhotoListState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadPhotos() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PhotoItem(photo: Photo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Добавляем placeholder и error
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.downloadUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Photo by ${photo.author}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop,
                onError = { error ->
                    println("❌ Error loading image: ${error.result.throwable.message}")
                }
            )
            Text(
                text = photo.author,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}