package com.example.demo.services.tasks.impl

import com.example.demo.mappers.TaskMapper
import com.example.demo.models.tasks.CreateTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.repositories.tasks.TaskRepository
import com.example.demo.services.tasks.TaskService
import org.springframework.stereotype.Service

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {
    override fun getTasks(): List<TaskDto> {
        return taskRepository.findAll()
            .map { TaskMapper.toDto(it) }
            .toList()
    }

    override fun createTask(request: CreateTaskDto) {
        val entity = TaskMapper.toEntity(request)
        taskRepository.save(entity)
    }
}