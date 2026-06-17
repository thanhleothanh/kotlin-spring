package com.example.demo.services.tasks

import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.models.tasks.TaskStatsDto

interface TaskService {
    fun getTasks(userId: Long): List<TaskDto>
    fun postTask(userId: Long, request: PostTaskDto): TaskDto
    fun patchTask(userId: Long, id: Long, request: PatchTaskDto): TaskDto
    fun deleteTask(userId: Long, id: Long)
    suspend fun getTaskStats(userId: Long): TaskStatsDto
}
