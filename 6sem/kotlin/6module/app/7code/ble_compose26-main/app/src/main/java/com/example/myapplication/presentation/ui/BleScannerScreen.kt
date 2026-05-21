package com.example.myapplication.presentation.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BleScannerScreen() {
    val context = LocalContext.current
    val viewModel = remember { BleViewModel(context = context) }

    val devices by viewModel.devices.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    // Состояние для хранения безопасно полученных имен устройств
    var deviceNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Список разрешений в зависимости от версии Android
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

    // Отдельное разрешение для BLUETOOTH_CONNECT на Android 12+
    val bluetoothConnectPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rememberPermissionState(Manifest.permission.BLUETOOTH_CONNECT)
    } else null

    // Безопасное получение имени устройства
    fun getDeviceName(device: BluetoothDevice): String {
        return try {
            device.name ?: "Без имени"
        } catch (e: SecurityException) {
            println("SecurityException getting device name: ${e.message}")
            "Неизвестное устройство"
        } catch (e: Exception) {
            "Ошибка доступа"
        }
    }

    // Безопасное получение адреса устройства
    fun getDeviceAddress(device: BluetoothDevice): String {
        return try {
            device.address
        } catch (e: SecurityException) {
            println("SecurityException getting device address: ${e.message}")
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Монитор сердечного ритма", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (permissionState.allPermissionsGranted) {
                try {
                    if (isScanning) viewModel.stopScan() else viewModel.startScan()
                } catch (e: SecurityException) {
                    println("SecurityException during scan operation: ${e.message}")
                    permissionState.launchMultiplePermissionRequest()
                }
            } else {
                permissionState.launchMultiplePermissionRequest()
            }
        }) {
            Text(if (isScanning) "Перезапустить сканирование" else "Начать сканирование")
        }

        if (!permissionState.allPermissionsGranted) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Требуются разрешения Bluetooth и локации",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Heart Rate: ${heartRate ?: "—"}",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Статус: $connectionState",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (devices.isEmpty() && !isScanning) {
            Text(
                text = "Устройства не найдены",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = "Доступные устройства:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyColumn {
            items(devices) { device ->
                // Безопасное получение имени и адреса
                val deviceName = remember(device) { getDeviceName(device) }
                val deviceAddress = remember(device) { getDeviceAddress(device) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Проверяем разрешение перед подключением
                            if (bluetoothConnectPermissionState != null &&
                                !bluetoothConnectPermissionState.status.isGranted) {
                                bluetoothConnectPermissionState.launchPermissionRequest()
                            } else if (permissionState.allPermissionsGranted) {
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
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = deviceAddress,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (connectionState == "Connected") {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    try {
                        viewModel.refreshData()
                    } catch (e: SecurityException) {
                        println("SecurityException during refresh: ${e.message}")
                    }
                }) {
                    Text("Обновить данные")
                }
                Button(onClick = {
                    try {
                        viewModel.disconnect()
                    } catch (e: SecurityException) {
                        println("SecurityException during disconnect: ${e.message}")
                    }
                }) {
                    Text("Отключиться")
                }
            }
        }
    }
}