package com.example.firstapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import com.example.firstapplication.data.DiaryEntry

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _entries = MutableStateFlow<List<DiaryEntry>>(emptyList())
    val entries: StateFlow<List<DiaryEntry>> = _entries.asStateFlow()
    
    private val _currentEntry = MutableStateFlow<DiaryEntry?>(null)
    val currentEntry: StateFlow<DiaryEntry?> = _currentEntry.asStateFlow()
    
    private val filesDir: File = application.filesDir
    // ↑ Это Internal Storage: /data/data/<package>/files/
    init {
        loadAllEntries()
    }
    
    private fun loadAllEntries() {
        viewModelScope.launch {
            val files = filesDir.listFiles { file ->
                file.isFile && file.name.endsWith(".txt")
            }?.map { DiaryEntry.fromFile(it) }?.sortedByDescending { it.timestamp }
                ?: emptyList()
            _entries.value = files
        }
    }
    
    fun saveEntry(title: String, content: String, existingFilename: String? = null) {
        viewModelScope.launch {
            val timestamp = existingFilename?.let { 
                DiaryEntry.parseFilename(it).first 
            } ?: System.currentTimeMillis()
            
            val filename = if (existingFilename != null) {
                existingFilename
            } else {
                DiaryEntry.createFilename(title, timestamp)
            }
            
            val file = File(filesDir, filename)
            file.writeText(content)  // ← Запись в файл
            
            val newEntry = DiaryEntry(
                filename = filename,
                title = title,
                content = content,
                timestamp = timestamp
            )
            
            if (existingFilename != null) {
                val index = _entries.value.indexOfFirst { it.filename == existingFilename }
                if (index != -1) {
                    val updatedList = _entries.value.toMutableList()
                    updatedList[index] = newEntry
                    _entries.value = updatedList
                }
            } else {
                _entries.value = listOf(newEntry) + _entries.value
            }
        }
    }
    
    fun deleteEntry(filename: String) {
        viewModelScope.launch {
            val file = File(filesDir, filename)
            if (file.exists()) {
                file.delete()
                _entries.value = _entries.value.filter { it.filename != filename }
            }
        }
    }
    
    fun selectEntry(entry: DiaryEntry) {
        _currentEntry.value = entry
    }
    
    fun clearCurrentEntry() {
        _currentEntry.value = null
    }
}
