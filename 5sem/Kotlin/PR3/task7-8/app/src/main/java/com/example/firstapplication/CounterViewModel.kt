package com.example.firstapplication

import androidx.lifecycle.ViewModel

class CounterViewModel : ViewModel() {
    
    private var _counter = 0
    val counter: Int
        get() = _counter
    
    fun incrementCounter() {
        _counter++
    }
}

