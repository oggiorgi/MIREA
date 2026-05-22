package com.example.heartrate.presentation

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.heartrate.data.BleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BleViewModel(private val repository: BleRepository) : ViewModel() {
    private var _connectedDeviceAddress = MutableStateFlow<String?>(null)
    val connectedDeviceAddress: StateFlow<String?> = _connectedDeviceAddress
    val devices: StateFlow<List<BluetoothDevice>> = repository.devices
    val heartRate: StateFlow<Int?> = repository.heartRate
    val connectionState: StateFlow<String> = repository.connectionState
    val isScanning: StateFlow<Boolean> = repository.isScanning

    fun startScan() = viewModelScope.launch { repository.startScan() }

    fun stopScan() = viewModelScope.launch { repository.stopScan() }

    fun connect(device: BluetoothDevice) {
        _connectedDeviceAddress.value = device.address
        viewModelScope.launch { repository.connect(device) }
    }

    fun disconnect() {
        _connectedDeviceAddress.value = null
        repository.disconnect()
    }
}