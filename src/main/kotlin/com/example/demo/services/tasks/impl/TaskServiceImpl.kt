package com.example.demo.services.tasks.impl

import com.example.demo.entities.TaskEntity
import com.example.demo.events.task.TaskEvent
import com.example.demo.mappers.TaskMapper
import com.example.demo.models.tasks.*
import com.example.demo.repositories.tasks.TaskRepository
import com.example.demo.repositories.users.UserRepository
import com.example.demo.services.tasks.TaskService
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
) : TaskService {

    override fun getTasks(userId: Long): List<TaskDto> {
        return taskRepository.findAllByUserId(userId)
            .map { TaskMapper.toDto(it) }
    }

    override fun postTask(userId: Long, request: PostTaskDto): TaskDto {
        val user = userRepository.getReferenceById(userId)
        val entity = TaskMapper.toEntity(request, user)
        val saved = taskRepository.save(entity)

        eventPublisher.publishEvent(
            TaskEvent.Created(
                taskId = requireNotNull(saved.id),
                userId = userId,
                title = saved.title,
                status = saved.status,
            )
        )

        return TaskMapper.toDto(saved)
    }

    override fun patchTask(userId: Long, id: Long, request: PatchTaskDto): TaskDto {
        val entity = getTaskByIdAndUser(id, userId)
        val oldStatus = entity.status
        val changedFields = mutableMapOf<String, Any?>()

        request.title.ifPresent { title ->
            entity.title = title
            changedFields["title"] = title
        }
        request.description.ifPresent { description ->
            entity.description = description
            changedFields["description"] = description
        }
        request.status.ifPresent { newStatus ->
            entity.status = newStatus
            changedFields["status"] = newStatus
            when (newStatus.onTransitionFrom(oldStatus)) {
                TaskStatus.CompletedAtEffect.Set -> entity.completedAt = Instant.now()
                TaskStatus.CompletedAtEffect.Clear -> entity.completedAt = null
                null -> {}
            }
        }

        val updated = taskRepository.save(entity)

        if (changedFields.isNotEmpty()) {
            eventPublisher.publishEvent(
                TaskEvent.Updated(
                    taskId = id,
                    userId = userId,
                    changedFields = changedFields,
                )
            )
        }

        return TaskMapper.toDto(updated)
    }

    override fun deleteTask(userId: Long, id: Long) {
        val entity = getTaskByIdAndUser(id, userId)
        taskRepository.delete(entity)

        eventPublisher.publishEvent(
            TaskEvent.Deleted(
                taskId = id,
                userId = userId,
            )
        )
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

    private fun getTaskByIdAndUser(id: Long, userId: Long): TaskEntity {
        return taskRepository.findByIdAndUserId(id, userId)
            ?: throw EntityNotFoundException("Task not found with id $id")
    }
}
