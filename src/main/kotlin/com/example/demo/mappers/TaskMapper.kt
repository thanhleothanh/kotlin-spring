package com.example.demo.mappers

import com.example.demo.entities.TaskEntity
import com.example.demo.models.tasks.CreateTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.models.tasks.TaskStatus

object TaskMapper {

    fun toDto(entity: TaskEntity): TaskDto {
        return TaskDto(
            id = requireNotNull(entity.id) { "Entity ID must not be null when mapping to a response DTO" },
            title = entity.title,
            description = entity.description,
            status = entity.status,
            completedAt = entity.completedAt,
        )
    }

    fun toEntity(request: CreateTaskDto): TaskEntity {
        return TaskEntity(
            title = request.title,
            status = TaskStatus.OPEN,
            description = request.description,
            completedAt = null
        )
    }
}