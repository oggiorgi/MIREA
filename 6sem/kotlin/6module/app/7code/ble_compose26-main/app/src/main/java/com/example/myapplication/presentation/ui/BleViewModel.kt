package com.example.myapplication.presentation.ui

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.BleRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BleViewModel(
    context: Context
) : ViewModel() {

    private val repository: BleRepository = BleRepository(context.applicationContext)

    val devices: StateFlow<List<BluetoothDevice>> = repository.devices
    val heartRate: StateFlow<String?> = repository.heartRate
    val connectionState: StateFlow<String> = repository.connectionState
    val isScanning: StateFlow<Boolean> = repository.isScanning

    fun refreshData() = viewModelScope.launch {
        repository.refreshData()
    }

    fun startScan() = viewModelScope.launch {
        repository.startScan()
    }

    fun stopScan() = viewModelScope.launch {
        repository.stopScan()
    }

    fun connect(device: BluetoothDevice) = viewModelScope.launch {
        repository.connect(device)
    }

    fun disconnect() {
        repository.disconnect()
    }

    override fun onCleared() {
        repository.disconnect()
        super.onCleared()
    }
}