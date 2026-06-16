package com.example.demo.services.tasks.impl

import com.example.demo.entities.TaskEntity
import com.example.demo.mappers.TaskMapper
import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.models.tasks.TaskStatus
import com.example.demo.repositories.tasks.TaskRepository
import com.example.demo.services.tasks.TaskService
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class TaskServiceImpl(
    private val taskRepository: TaskRepository
) : TaskService {
    override fun getTasks(): List<TaskDto> {
        return taskRepository.findAll()
            .map { TaskMapper.toDto(it) }
            .toList()
    }

    override fun postTask(request: PostTaskDto): TaskDto {
        val entity = TaskMapper.toEntity(request)
        return TaskMapper.toDto(taskRepository.save(entity))
    }

    override fun patchTask(id: Long, request: PatchTaskDto): TaskDto {
        val entity = getTaskById(id)
        applyPatch(entity, request)
        return TaskMapper.toDto(taskRepository.save(entity))
    }

    override fun deleteTask(id: Long) {
        val entity = getTaskById(id)
        taskRepository.delete(entity)
    }

    private fun applyPatch(entity: TaskEntity, request: PatchTaskDto) {
        if (request.title.isPresent) {
            entity.title = request.title.get()
        }
        if (request.description.isPresent) {
            entity.description = request.description.get()
        }
        if (request.status.isPresent) {
            val oldStatus = entity.status
            val newStatus = request.status.get()
            entity.status = newStatus

            when (newStatus.onTransitionFrom(oldStatus)) {
                TaskStatus.CompletedAtEffect.Set -> entity.completedAt = Instant.now()
                TaskStatus.CompletedAtEffect.Clear -> entity.completedAt = null
                null -> {}
            }
        }
    }

    private fun getTaskById(id: Long): TaskEntity {
        return taskRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Task not found with id $id") }
    }
}
