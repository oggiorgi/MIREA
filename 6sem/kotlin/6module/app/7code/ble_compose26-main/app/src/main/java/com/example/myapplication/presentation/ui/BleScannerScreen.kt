package com.example.myapplication.presentation.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BleScannerScreen() {
    val context = LocalContext.current
    val viewModel = remember { BleViewModel(context = context) }

    val devices by viewModel.devices.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val permissionsList = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    val permissionState = rememberMultiplePermissionsState(permissionsList)

    fun getDeviceName(device: BluetoothDevice): String {
        return try {
            device.name ?: "Без имени"
        } catch (e: SecurityException) {
            "Неизвестное устройство"
        } catch (e: Exception) {
            "Ошибка доступа"
        }
    }

    fun getDeviceAddress(device: BluetoothDevice): String {
        return try {
            device.address
        } catch (e: SecurityException) {
            "Адрес недоступен"
        } catch (e: Exception) {
            "Ошибка доступа"
        }
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted && !isScanning) {
            try {
                viewModel.startScan()
            } catch (e: SecurityException) {
                println("SecurityException during scan: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "BLE Scanner",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка сканирования
            Button(
                onClick = {
                    if (permissionState.allPermissionsGranted) {
                        try {
                            if (isScanning) viewModel.stopScan() else viewModel.startScan()
                        } catch (e: SecurityException) {
                            permissionState.launchMultiplePermissionRequest()
                        }
                    } else {
                        permissionState.launchMultiplePermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isScanning) "Перезапустить сканирование" else "Начать сканирование")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Пульс и статус
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Пульс: ${heartRate ?: "—"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Статус: $connectionState",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (connectionState == "Connected")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Список устройств
            if (devices.isNotEmpty()) {
                LazyColumn {
                    items(devices) { device ->
                        val deviceName = remember(device) { getDeviceName(device) }
                        val deviceAddress = remember(device) { getDeviceAddress(device) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (permissionState.allPermissionsGranted) {
                                        try {
                                            viewModel.connect(device)
                                        } catch (e: SecurityException) {
                                            println("SecurityException during connect: ${e.message}")
                                        }
                                    } else {
                                        permissionState.launchMultiplePermissionRequest()
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = deviceName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = deviceAddress,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else if (!isScanning && permissionState.allPermissionsGranted) {
                Text(
                    text = "Устройства не найдены",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки управления когда подключены
            if (connectionState == "Connected") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                viewModel.refreshData()
                            } catch (e: SecurityException) {
                                println("SecurityException during refresh: ${e.message}")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Обновить данные")
                    }
                    Button(
                        onClick = {
                            try {
                                viewModel.disconnect()
                            } catch (e: SecurityException) {
                                println("SecurityException during disconnect: ${e.message}")
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Отключиться")
                    }
                }
            }
        }
    }
}