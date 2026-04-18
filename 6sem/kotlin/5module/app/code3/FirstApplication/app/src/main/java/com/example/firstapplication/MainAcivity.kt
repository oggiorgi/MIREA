package com.example.firstapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.firstapplication.ui.theme.AppTheme
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = true) {
                GalleryApp()
            }
        }
    }
}

@Composable
fun GalleryApp() {
    val context = LocalContext.current
    var photos by remember { mutableStateOf<List<File>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Лаунчер для результата камеры
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            photos = loadPhotos(context)
        }
    }

    // Лаунчер для запроса разрешения
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera(context, cameraLauncher)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Нужно разрешение на камеру!")
            }
        }
    }

    // Лаунчер для запроса прав на память (только для старых Android)
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Без разрешения на память экспорт не сработает!")
            }
        }
    }

// Запрашиваем права на память при старте, если Android старый
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                storagePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    // Загружаем фото при старте
    LaunchedEffect(Unit) {
        photos = loadPhotos(context)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
                if (hasPermission) {
                    launchCamera(context, cameraLauncher)
                } else {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Сделать фото")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (photos.isEmpty()) {
                EmptyGalleryScreen {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        launchCamera(context, cameraLauncher)
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                }
            } else {
                PhotoGrid(
                    photos = photos,
                    onExportToGallery = { file ->
                        scope.launch {
                            // 1. Проверяем версию Android
                            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                                // Для Android 9 и ниже нужно запрашивать WRITE_EXTERNAL_STORAGE
                                val hasWritePermission = ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED

                                if (!hasWritePermission) {
                                    // Если прав нет, показываем сообщение или запрашиваем их
                                    // Для простоты в этом задании покажем Snackbar с просьбой дать права в настройках,
                                    // т.к. запрос прав из корутины сложен.
                                    // Но лучше всего просто добавить проверку в MainActivity onCreate или при старте.

                                    snackbarHostState.showSnackbar("⚠️ На этом Android нужно разрешение на память в настройках!")
                                    return@launch
                                }
                            }

                            // 2. Выполняем экспорт
                            val success = exportToGallery(context, file)
                            if (success) {
                                snackbarHostState.showSnackbar("✅ Фото добавлено в галерею")
                            } else {
                                snackbarHostState.showSnackbar("❌ Ошибка при экспорте")
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyGalleryScreen(onTakePhoto: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "У вас пока нет фото\nСделайте первое!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTakePhoto) {
            Text("Сделать первое фото")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGrid(photos: List<File>, onExportToGallery: (File) -> Unit) {
    // Состояние для хранения имени файла, у которого открыто меню.
    // Если null — меню закрыто везде.
    var expandedFileName by remember { mutableStateOf<String?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { file ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // 1. Изображение
                AsyncImage(
                    model = file,
                    contentDescription = "Фото",
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(android.R.drawable.ic_menu_report_image),
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                )

                // 2. Кнопка с тремя точками (меню)
                IconButton(
                    onClick = {
                        // Открываем меню именно для этого файла
                        expandedFileName = file.name
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                            RoundedCornerShape(50) // Круглый фон под кнопкой
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Меню",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 3. Dropdown меню
                // Показываем меню только если имя текущего файла совпадает с expandedFileName
                if (expandedFileName == file.name) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = { expandedFileName = null }, // Закрываем при клике вне меню
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("📤 Экспорт в галерею") },
                            onClick = {
                                expandedFileName = null // Сначала закрываем меню
                                onExportToGallery(file) // Потом запускаем экспорт
                            }
                        )
                    }
                }
            }
        }
    }
}

// Запуск камеры
fun launchCamera(context: Context, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val photoFile = createImageFile(context)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, uri)
        addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
    launcher.launch(intent)
}

// Создание пути для файла
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "IMG_$timeStamp.jpg"
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    return File(storageDir, imageFileName)
}

// Загрузка фото из папки
fun loadPhotos(context: Context): List<File> {
    val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES) ?: return emptyList()
    return dir.listFiles()
        ?.filter { it.extension.lowercase() == "jpg" && it.length() > 0 }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()
}

// Экспорт в галерею через MediaStore
fun exportToGallery(context: Context, sourceFile: File): Boolean {
    return try {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            // 🔹 LEGACY способ для Android 8.1 и 9
            @Suppress("DEPRECATION")
            val picturesDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_PICTURES
            )
            val exportDir = File(picturesDir, "MyGalleryExport")

            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val destFile = File(exportDir, sourceFile.name)
            sourceFile.copyTo(destFile, overwrite = true)

            // Уведомляем систему о новом файле (чтобы он появился в галерее)
            context.sendBroadcast(
                android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    .setData(android.net.Uri.fromFile(destFile))
            )
            true

        } else {
            // 🔹 MODERN способ для Android 10+ (Scoped Storage)
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, sourceFile.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/MyGalleryExport")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return false

            context.contentResolver.openOutputStream(uri)?.use { output ->
                sourceFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            val updateValues = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            context.contentResolver.update(uri, updateValues, null, null)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}