package com.example.demo.models.tasks

import java.time.Instant

data class TaskDto(
    val id: Long,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val completedAt: Instant?
)