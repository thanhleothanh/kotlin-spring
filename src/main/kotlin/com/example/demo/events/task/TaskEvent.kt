package com.example.demo.events.task

import com.example.demo.models.tasks.TaskStatus

sealed class TaskEvent(
    open val taskId: Long,
    open val userId: Long,
    open val eventType: String,
) {
    data class Created(
        override val taskId: Long,
        override val userId: Long,
        val title: String,
        val status: TaskStatus,
    ) : TaskEvent(taskId = taskId, userId = userId, eventType = "task.created")

    data class Updated(
        override val taskId: Long,
        override val userId: Long,
        val changedFields: Map<String, Any?>,
    ) : TaskEvent(taskId = taskId, userId = userId, eventType = "task.updated")

    data class Deleted(
        override val taskId: Long,
        override val userId: Long,
    ) : TaskEvent(taskId = taskId, userId = userId, eventType = "task.deleted")
}
