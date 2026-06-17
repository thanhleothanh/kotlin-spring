package com.example.demo.services.tasks.impl

import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.models.tasks.TaskStatus
import com.example.demo.services.tasks.TaskService
import jakarta.persistence.EntityNotFoundException
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class InMemoryTaskServiceImpl : TaskService {

    private data class UserTasks(
        val tasks: MutableMap<Long, InMemoryTask> = ConcurrentHashMap(),
        val idCounter: AtomicLong = AtomicLong(0),
    )

    private val userTasks = ConcurrentHashMap<Long, UserTasks>()

    private data class InMemoryTask(
        var title: String,
        var description: String?,
        var status: TaskStatus,
        var completedAt: Instant?,
    )

    override fun getTasks(userId: Long): List<TaskDto> {
        val userStore = userTasks[userId] ?: return emptyList()
        return userStore.tasks.entries
            .sortedBy { it.key }
            .map { it.value.toDto(it.key) }
    }

    override fun postTask(userId: Long, request: PostTaskDto): TaskDto {
        val userStore = userTasks.getOrPut(userId) { UserTasks() }
        val id = userStore.idCounter.incrementAndGet()
        val task = InMemoryTask(
            title = request.title,
            description = request.description,
            status = request.status,
            completedAt = null,
        )
        userStore.tasks[id] = task
        return task.toDto(id)
    }

    override fun patchTask(userId: Long, id: Long, request: PatchTaskDto): TaskDto {
        val userStore = userTasks[userId] ?: throw EntityNotFoundException("Task not found with id $id")
        val task = userStore.tasks[id] ?: throw EntityNotFoundException("Task not found with id $id")

        request.title.ifPresent { task.title = it }
        request.description.ifPresent { task.description = it }
        request.status.ifPresent { newStatus ->
            val oldStatus = task.status
            task.status = newStatus
            when (newStatus.onTransitionFrom(oldStatus)) {
                TaskStatus.CompletedAtEffect.Set -> task.completedAt = Instant.now()
                TaskStatus.CompletedAtEffect.Clear -> task.completedAt = null
                null -> {}
            }
        }

        return task.toDto(id)
    }

    override fun deleteTask(userId: Long, id: Long) {
        val userStore = userTasks[userId] ?: throw EntityNotFoundException("Task not found with id $id")
        if (userStore.tasks.remove(id) == null) {
            throw EntityNotFoundException("Task not found with id $id")
        }
    }

    private fun InMemoryTask.toDto(id: Long) = TaskDto(
        id = id,
        title = title,
        description = description,
        status = status,
        completedAt = completedAt,
    )
}
