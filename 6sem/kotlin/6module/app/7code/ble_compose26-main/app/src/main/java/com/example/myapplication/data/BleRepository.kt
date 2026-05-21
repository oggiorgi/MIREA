package com.example.myapplication.data

import android.Manifest
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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class BleRepository(context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val appContext = context.applicationContext

    private val bluetoothManager = appContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter = bluetoothManager.adapter

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val devices: StateFlow<List<BluetoothDevice>> = _devices

    private val _heartRate = MutableStateFlow<String?>(null)
    val heartRate: StateFlow<String?> = _heartRate

    private val _connectionState = MutableStateFlow<String>("Disconnected")
    val connectionState: StateFlow<String> = _connectionState

    private var currentGatt: BluetoothGatt? = null

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (!device.name.isNullOrBlank()) {
                println("Найдено устройство: ${device.name} - ${device.address}")

                val current = _devices.value.toMutableList()
                if (current.none { it.address == device.address }) {
                    current.add(device)
                    _devices.value = current.toList()
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _isScanning.value = false
            println("Сканирование провалилось: errorCode = $errorCode")
        }
    }

    private fun hasBluetoothScanPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // На старых версиях не требуется
        }
    }

    fun startScan() {
        if (!hasBluetoothScanPermission()) {
            println("Нет разрешения на сканирование Bluetooth")
            return
        }

        try {
            if (!adapter.isEnabled) {
                println("Bluetooth не включен")
                return
            }

            val scanner = adapter.bluetoothLeScanner
            if (scanner == null) {
                println("BluetoothLeScanner недоступен")
                return
            }

            _devices.value = emptyList()

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            scanner.startScan(null, settings, scanCallback)
            _isScanning.value = true
            println("Сканирование запущено")
        } catch (e: SecurityException) {
            println("SecurityException in startScan: ${e.message}")
            _isScanning.value = false
        } catch (e: Exception) {
            println("Exception in startScan: ${e.message}")
            _isScanning.value = false
        }
    }

    fun stopScan() {
        if (!hasBluetoothScanPermission()) {
            println("Нет разрешения на остановку сканирования Bluetooth")
            return
        }

        try {
            adapter.bluetoothLeScanner?.stopScan(scanCallback)
            _isScanning.value = false
            println("Сканирование остановлено")
        } catch (e: SecurityException) {
            println("SecurityException in stopScan: ${e.message}")
        } catch (e: Exception) {
            println("Exception in stopScan: ${e.message}")
        }
    }

    fun connect(device: BluetoothDevice) {
        if (!hasBluetoothConnectPermission()) {
            println("Нет разрешения на подключение к Bluetooth")
            return
        }

        try {
            stopScan()
            currentGatt = device.connectGatt(appContext, false, gattCallback)
            _connectionState.value = "Connecting"
            println("Подключение к ${device.address}...")
        } catch (e: SecurityException) {
            println("SecurityException in connect: ${e.message}")
            _connectionState.value = "Disconnected"
        } catch (e: Exception) {
            println("Exception in connect: ${e.message}")
            _connectionState.value = "Disconnected"
        }
    }

    fun disconnect() {
        try {
            currentGatt?.disconnect()
            currentGatt?.close()
            currentGatt = null
            _connectionState.value = "Disconnected"
            _heartRate.value = null
            println("Отключено")
        } catch (e: Exception) {
            println("Exception in disconnect: ${e.message}")
        }
    }

    fun refreshData() {
        if (!hasBluetoothConnectPermission()) {
            println("Нет разрешения для чтения данных")
            return
        }

        try {
            currentGatt?.let { gatt ->
                val service = gatt.getService(HEART_RATE_SERVICE_UUID)
                if (service == null) {
                    println("Heart Rate Service не найден")
                    return
                }

                val characteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_UUID)
                if (characteristic == null) {
                    println("Heart Rate Measurement не найдена")
                    return
                }

                val success = gatt.readCharacteristic(characteristic)
                println("Ручное обновление данных: $success")
            }
        } catch (e: SecurityException) {
            println("SecurityException in refreshData: ${e.message}")
        } catch (e: Exception) {
            println("Exception in refreshData: ${e.message}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    _connectionState.value = "Connected"
                    println("Подключено, обнаруживаем сервисы...")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = "Disconnected"
                    _heartRate.value = null
                    println("Отключено")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            println("onServicesDiscovered: status = $status")

            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(HEART_RATE_SERVICE_UUID)
                if (service == null) {
                    println("Heart Rate Service НЕ НАЙДЕН!")
                    return
                }
                println("Heart Rate Service найден")

                val characteristic = service.getCharacteristic(HEART_RATE_MEASUREMENT_UUID)
                if (characteristic == null) {
                    println("Heart Rate Measurement НЕ НАЙДЕНА!")
                    return
                }
                println("Heart Rate Measurement найдена")

                try {
                    // Включаем уведомления
                    val success = gatt.setCharacteristicNotification(characteristic, true)
                    println("setCharacteristicNotification: $success")

                    val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                    if (descriptor != null) {
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                        println("Запись CCC-дескриптора отправлена")
                    } else {
                        println("CCC дескриптор не найден")
                    }

                    // Первоначальное чтение
                    gatt.readCharacteristic(characteristic)
                } catch (e: SecurityException) {
                    println("SecurityException in onServicesDiscovered: ${e.message}")
                } catch (e: Exception) {
                    println("Exception in onServicesDiscovered: ${e.message}")
                }
            } else {
                println("Обнаружение сервисов провалилось: status = $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            println("onCharacteristicChanged: uuid = ${characteristic.uuid}")
            parseHeartRateData(characteristic)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            println("onCharacteristicRead: status = $status, uuid = ${characteristic.uuid}")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                parseHeartRateData(characteristic)
            } else {
                println("Ошибка чтения: status = $status")
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            println("onDescriptorWrite: status = $status, uuid = ${descriptor.uuid}")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                println("Дескриптор CCC успешно записан — уведомления должны работать")
            } else {
                println("Ошибка записи дескриптора: $status")
            }
        }

        private fun parseHeartRateData(characteristic: BluetoothGattCharacteristic) {
            val value = characteristic.value ?: run {
                println("Значение характеристики пустое (null)")
                return
            }

            if (value.isEmpty()) {
                println("Значение характеристики пустое (0 байт)")
                return
            }

            println("Получено значение (hex): ${value.joinToString(" ") { "%02x".format(it) }}")

            val flags = value[0].toInt() and 0xFF
            val isHeartRate16Bit = (flags and 0x01) != 0

            var offset = 1
            val heartRate = if (isHeartRate16Bit && value.size >= offset + 2) {
                ((value[offset + 1].toInt() and 0xFF) shl 8) or (value[offset].toInt() and 0xFF)
            } else if (value.size >= offset + 1) {
                value[offset].toInt() and 0xFF
            } else {
                println("Недостаточно данных для парсинга пульса")
                return
            }

            val heartRateText = "$heartRate bpm"
            _heartRate.value = heartRateText
            println("Пульс: $heartRateText")
        }
    }

    companion object {
        private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val HEART_RATE_MEASUREMENT_UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}