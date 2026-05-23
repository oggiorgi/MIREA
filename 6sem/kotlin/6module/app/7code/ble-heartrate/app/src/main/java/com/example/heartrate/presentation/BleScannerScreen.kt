package com.example.heartrate.presentation

import android.Manifest
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.heartrate.data.BleRepository
import com.google.accompanist.permissions.*
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BleScannerScreen() {
    val context = LocalContext.current
    val repository = remember { BleRepository(context) }
    val viewModel: BleViewModel = viewModel(
        factory = BleViewModelFactory(repository)
    )

    val devices by viewModel.devices.collectAsState()
    val heartRate by viewModel.heartRate.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(permissions.allPermissionsGranted) {
        if (permissions.allPermissionsGranted && !isScanning) {
            viewModel.startScan()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val connectedDeviceAddress by viewModel.connectedDeviceAddress.collectAsState()

        Spacer(modifier = Modifier.height(30.dp))

        Text("BLE Heart Rate Monitor", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (permissions.allPermissionsGranted) {
                if (isScanning) viewModel.stopScan() else viewModel.startScan()
            } else {
                permissions.launchMultiplePermissionRequest()
            }
        }) {
            Text(if (isScanning) "Перезапустить сканирование" else "Начать сканирование")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Heart Rate: ${heartRate ?: "-"} bpm",
            style = MaterialTheme.typography.titleLarge
        )
        Text("Status: $connectionState", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (devices.isEmpty() && !isScanning) {
            Text("No devices found")
        }

        LazyColumn {
            items(devices) { device ->
                DeviceCard(
                    device = device,
                    onClick = { viewModel.connect(device) },
                    isConnected = connectionState == "Connected" && device.address == connectedDeviceAddress
                )
            }
        }

        if (connectionState == "Connected") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { viewModel.disconnect() }) {
                    Text("Disconnect")
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceCard(
    device: BluetoothDevice,
    onClick: () -> Unit,
    isConnected: Boolean = false
) {
    val deviceName = try {
        device.name ?: "Unknown"
    } catch (e: SecurityException) {
        "Unknown"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(deviceName, style = MaterialTheme.typography.titleMedium)
            Text(device.address, style = MaterialTheme.typography.bodySmall)
            if (isConnected) {
                Text(
                    "Connected",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}