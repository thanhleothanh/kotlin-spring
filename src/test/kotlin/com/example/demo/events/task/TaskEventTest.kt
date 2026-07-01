package com.example.demo.events.task

import com.example.demo.models.tasks.TaskStatus
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskEventTest {

    @Test
    fun `created event has correct eventType`() {
        val event = TaskEvent.Created(
            taskId = 1L,
            userId = 42L,
            title = "Test",
            status = TaskStatus.OPEN,
        )
        assertEquals("task.created", event.eventType)
    }

    @Test
    fun `updated event has correct eventType`() {
        val event = TaskEvent.Updated(
            taskId = 1L,
            userId = 42L,
            changedFields = mapOf("title" to "New Title"),
        )
        assertEquals("task.updated", event.eventType)
    }

    @Test
    fun `deleted event has correct eventType`() {
        val event = TaskEvent.Deleted(
            taskId = 1L,
            userId = 42L,
        )
        assertEquals("task.deleted", event.eventType)
    }
}
