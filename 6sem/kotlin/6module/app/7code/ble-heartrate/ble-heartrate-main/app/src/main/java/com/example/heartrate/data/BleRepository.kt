package com.example.heartrate.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

@SuppressLint("MissingPermission")
class BleRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate

    private val _connectionState = MutableStateFlow("Disconnected")
    val connectionState: StateFlow<String> = _connectionState

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private var currentGatt: BluetoothGatt? = null

    companion object {
        private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val HEART_RATE_MEASUREMENT_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (!device.name.isNullOrBlank()) {
                val current = _devices.value.toMutableList()
                if (current.none { it.address == device.address }) {
                    current.add(device)
                    _devices.value = current
                }
            }
        }
        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
        }
    }

    fun startScan() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return
        if (!adapter.isEnabled) return
        val scanner = adapter.bluetoothLeScanner ?: return
        _devices.value = emptyList()
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner.startScan(null, settings, scanCallback)
        _isScanning.value = true
    }

    fun stopScan() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return
        adapter.bluetoothLeScanner?.stopScan(scanCallback)
        _isScanning.value = false
    }

    fun connect(device: BluetoothDevice) {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return
        stopScan()
        currentGatt = device.connectGatt(context, false, gattCallback)
        _connectionState.value = "Connecting"
    }

    fun disconnect() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return
        currentGatt?.disconnect()
        currentGatt?.close()
        currentGatt = null
        _connectionState.value = "Disconnected"
        _heartRate.value = null
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = "Connected"
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = "Disconnected"
                    _heartRate.value = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(HEART_RATE_SERVICE_UUID)
                val characteristic = service?.getCharacteristic(HEART_RATE_MEASUREMENT_UUID)
                if (characteristic != null) {
                    gatt.setCharacteristicNotification(characteristic, true)
                    val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                    descriptor?.let {
                        it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(it)
                    }

                    scope.launch {
                        delay(500)
                        gatt.readCharacteristic(characteristic)
                        while (true) {
                            delay(5000)
                            gatt.readCharacteristic(characteristic)
                        }
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            parseHeartRate(value)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            println("onCharacteristicRead: status = $status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = characteristic.value
                parseHeartRate(value)
            }
        }
    }

    private fun parseHeartRate(data: ByteArray) {
        if (data.isEmpty()) {
            println("parseHeartRate: данные пустые")
            return
        }
        println("parseHeartRate: получено байт: ${data.size}")
        data.forEachIndexed { i, b ->
            println("  byte[$i] = 0x${b.toString(16)}")
        }
        val flags = data[0]
        val is16Bit = (flags.toInt() and 0x01) != 0
        val heartRate = if (is16Bit) {
            (data[2].toInt() and 0xFF) shl 8 or (data[1].toInt() and 0xFF)
        } else {
            data[1].toInt() and 0xFF
        }
        println("parseHeartRate: пульс = $heartRate bpm")
        _heartRate.value = heartRate
    }
}
