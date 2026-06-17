package com.example.demo.models.tasks

data class TaskStatsDto(
    val totalTasks: Long,
    val openTasks: Long,
    val doneTasks: Long,
    val discardedTasks: Long,
    val recentTasks: List<TaskDto>,
)
