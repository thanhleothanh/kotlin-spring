package com.example.demo.services.tasks.impl

import com.example.demo.entities.TaskEntity
import com.example.demo.mappers.TaskMapper
import com.example.demo.models.tasks.*
import com.example.demo.repositories.tasks.TaskRepository
import com.example.demo.repositories.users.UserRepository
import com.example.demo.services.tasks.TaskService
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
) : TaskService {

    override fun getTasks(userId: Long): List<TaskDto> {
        return taskRepository.findAllByUserId(userId)
            .map { TaskMapper.toDto(it) }
    }

    override fun postTask(userId: Long, request: PostTaskDto): TaskDto {
        val user = userRepository.getReferenceById(userId)
        val entity = TaskMapper.toEntity(request, user)
        return TaskMapper.toDto(taskRepository.save(entity))
    }

    override fun patchTask(userId: Long, id: Long, request: PatchTaskDto): TaskDto {
        val entity = getTaskByIdAndUser(id, userId)
        applyPatch(entity, request)
        return TaskMapper.toDto(taskRepository.save(entity))
    }

    override fun deleteTask(userId: Long, id: Long) {
        val entity = getTaskByIdAndUser(id, userId)
        taskRepository.delete(entity)
    }

    override suspend fun getTaskStats(userId: Long): TaskStatsDto = coroutineScope {
        val totalDeferred = async(Dispatchers.IO) {
            taskRepository.countByUserId(userId)
        }
        val openDeferred = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatus(userId, TaskStatus.OPEN)
        }
        val doneDeferred = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatus(userId, TaskStatus.DONE)
        }
        val discardedDeferred = async(Dispatchers.IO) {
            taskRepository.countByUserIdAndStatus(userId, TaskStatus.DISCARDED)
        }
        val recentDeferred = async(Dispatchers.IO) {
            taskRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .map { TaskMapper.toDto(it) }
        }

        TaskStatsDto(
            totalTasks = totalDeferred.await(),
            openTasks = openDeferred.await(),
            doneTasks = doneDeferred.await(),
            discardedTasks = discardedDeferred.await(),
            recentTasks = recentDeferred.await(),
        )
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

    private fun getTaskByIdAndUser(id: Long, userId: Long): TaskEntity {
        return taskRepository.findByIdAndUserId(id, userId)
            ?: throw EntityNotFoundException("Task not found with id $id")
    }
}
