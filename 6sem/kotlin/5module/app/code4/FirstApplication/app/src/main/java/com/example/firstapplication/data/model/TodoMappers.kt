package com.example.firstapplication.data.model

import com.example.firstapplication.data.local.TodoEntity
import com.example.firstapplication.domain.model.TodoItem

fun TodoItemDto.toEntity(): TodoEntity = TodoEntity(
    title = title,
    description = description,
    isCompleted = isCompleted
)

fun TodoEntity.toDomain(): TodoItem = TodoItem(
    id = id,
    title = title,
    description = description,
    isCompleted = isCompleted
)