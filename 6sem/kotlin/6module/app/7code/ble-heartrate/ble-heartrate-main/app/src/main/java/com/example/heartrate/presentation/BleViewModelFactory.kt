package com.example.heartrate.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.heartrate.data.BleRepository

class BleViewModelFactory(private val repository: BleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BleViewModel(repository) as T
    }
}