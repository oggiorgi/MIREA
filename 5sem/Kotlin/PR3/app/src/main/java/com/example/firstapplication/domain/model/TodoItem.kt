package com.example.firstapplication.domain.model

data class TodoItem(
    val id: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean
)

