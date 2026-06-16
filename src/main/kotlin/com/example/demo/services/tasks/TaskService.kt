package com.example.demo.services.tasks

import com.example.demo.models.tasks.CreateTaskDto
import com.example.demo.models.tasks.TaskDto

interface TaskService {
    fun getTasks(): List<TaskDto>
    fun createTask(request: CreateTaskDto)
}