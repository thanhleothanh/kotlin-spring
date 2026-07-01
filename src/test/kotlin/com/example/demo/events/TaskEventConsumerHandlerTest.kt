package com.example.demo.events

import com.example.demo.events.core.EventEnvelope
import com.example.demo.events.task.TaskEvent
import com.example.demo.models.tasks.TaskStatus
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TaskEventConsumerHandlerTest {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @Test
    fun `creates correct event types from envelope`() {
        val events = listOf(
            TaskEvent.Created(1L, 42L, "Task", TaskStatus.OPEN) as TaskEvent,
            TaskEvent.Updated(1L, 42L, mapOf("title" to "New")),
            TaskEvent.Deleted(1L, 42L),
        )

        for (event in events) {
            val json = objectMapper.writeValueAsString(event)
            val envelope = EventEnvelope(
                eventType = event.eventType,
                correlationId = "corr-1",
                payload = json,
            )

            val deserialized = deserialize(envelope)

            assertEquals(event.javaClass, deserialized.javaClass)
            assertEquals(event.eventType, deserialized.eventType)
        }
    }

    @Test
    fun `created event round-trip preserves fields`() {
        val event = TaskEvent.Created(
            taskId = 1L,
            userId = 42L,
            title = "Test Task",
            status = TaskStatus.OPEN,
        )
        val json = objectMapper.writeValueAsString(event)
        val envelope = EventEnvelope(
            eventType = event.eventType,
            correlationId = "corr-1",
            payload = json,
        )

        val deserialized = deserialize(envelope) as TaskEvent.Created

        assertEquals(event.taskId, deserialized.taskId)
        assertEquals(event.userId, deserialized.userId)
        assertEquals(event.title, deserialized.title)
        assertEquals(event.status, deserialized.status)
    }

    @Test
    fun `updated event round-trip preserves fields`() {
        val event = TaskEvent.Updated(
            taskId = 1L,
            userId = 42L,
            changedFields = mapOf("title" to "New Title", "status" to "DONE"),
        )
        val json = objectMapper.writeValueAsString(event)
        val envelope = EventEnvelope(
            eventType = event.eventType,
            correlationId = "corr-1",
            payload = json,
        )

        val deserialized = deserialize(envelope) as TaskEvent.Updated

        assertEquals(event.taskId, deserialized.taskId)
        assertEquals(event.userId, deserialized.userId)
        assertEquals(event.changedFields, deserialized.changedFields)
    }

    @Test
    fun `deleted event round-trip preserves fields`() {
        val event = TaskEvent.Deleted(
            taskId = 1L,
            userId = 42L,
        )
        val json = objectMapper.writeValueAsString(event)
        val envelope = EventEnvelope(
            eventType = event.eventType,
            correlationId = "corr-1",
            payload = json,
        )

        val deserialized = deserialize(envelope) as TaskEvent.Deleted

        assertEquals(event.taskId, deserialized.taskId)
        assertEquals(event.userId, deserialized.userId)
    }

    private fun deserialize(envelope: EventEnvelope): TaskEvent {
        return when (envelope.eventType) {
            "task.created" -> objectMapper.readValue(envelope.payload, TaskEvent.Created::class.java)
            "task.updated" -> objectMapper.readValue(envelope.payload, TaskEvent.Updated::class.java)
            "task.deleted" -> objectMapper.readValue(envelope.payload, TaskEvent.Deleted::class.java)
            else -> throw IllegalArgumentException("Unknown event type: ${envelope.eventType}")
        }
    }
}
