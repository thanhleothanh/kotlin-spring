package com.example.demo.services.tasks

import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskDto

interface TaskService {
    fun getTasks(): List<TaskDto>
    fun postTask(request: PostTaskDto): TaskDto
    fun patchTask(id: Long, request: PatchTaskDto): TaskDto
    fun deleteTask(id: Long)
}
