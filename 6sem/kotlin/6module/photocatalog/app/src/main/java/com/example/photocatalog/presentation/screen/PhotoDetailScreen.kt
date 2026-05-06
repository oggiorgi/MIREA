package com.example.photocatalog.presentation.screen

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.photocatalog.data.repository.PhotoRepositoryImpl
import com.example.photocatalog.domain.entity.Photo
import com.example.photocatalog.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoDetailScreen(
    navController: NavController,
    photo: Photo
) {
    val context = LocalContext.current
    val repository: PhotoRepository = PhotoRepositoryImpl()
    var isDownloading by remember { mutableStateOf(false) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Для Android 13+ (API 33+) не нужно разрешение на запись
    val needPermission = Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 // Android 12 и ниже

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            downloadError = null
        } else {
            downloadError = "Необходимо разрешение для сохранения файлов"
        }
    }

    fun savePhoto() {
        when {
            !needPermission || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                downloadError = null
                isDownloading = true
                scope.launch {
                    downloadAndSavePhoto(repository, photo.downloadUrl, photo.author, context)
                    isDownloading = false
                }
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали фото") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                AsyncImage(
                    model = photo.downloadUrl,
                    contentDescription = "Photo by ${photo.author}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Автор:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = photo.author,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Размеры:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${photo.width} × ${photo.height}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ID фото:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = photo.id,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { savePhoto() },
                enabled = !isDownloading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Скачивание...")
                } else {
                    Text("Скачать фото")
                }
            }

            downloadError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

suspend fun downloadAndSavePhoto(
    repository: PhotoRepository,
    url: String,
    author: String,
    context: Context
) {
    val result = repository.downloadPhoto(url)
    result.fold(
        onSuccess = { imageData ->
            withContext(Dispatchers.IO) {
                try {
                    val fileName = "photo_${author}_${System.currentTimeMillis()}.jpg"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Для Android 10+ используем MediaStore
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }

                        val uri = context.contentResolver.insert(
                            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                            contentValues
                        )

                        uri?.let {
                            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                                outputStream.write(imageData)
                            }
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Фото сохранено в Downloads/$fileName",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        // Для Android 9 и ниже
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                        )
                        val file = java.io.File(downloadsDir, fileName)
                        java.io.FileOutputStream(file).use { outputStream ->
                            outputStream.write(imageData)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Фото сохранено в Downloads/$fileName",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Ошибка сохранения: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        },
        onFailure = { exception ->
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Ошибка скачивания: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )
}