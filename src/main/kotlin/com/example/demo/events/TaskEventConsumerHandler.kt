package com.example.demo.events

import com.example.demo.events.core.EventConsumer
import com.example.demo.events.task.TaskEvent
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class TaskEventConsumerHandler(
    private val eventConsumer: EventConsumer,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostConstruct
    fun setup() {
        eventConsumer.subscribe { envelope ->
            try {
                val taskEvent = deserialize(envelope.eventType, envelope.payload)

                when (taskEvent) {
                    is TaskEvent.Created -> logger.info("[TaskEventConsumerHandler] Consumed: task created id={}, title={}", taskEvent.taskId, taskEvent.title)
                    is TaskEvent.Updated -> logger.info("[TaskEventConsumerHandler] Consumed: task updated id={}, fields={}", taskEvent.taskId, taskEvent.changedFields)
                    is TaskEvent.Deleted -> logger.info("[TaskEventConsumerHandler] Consumed: task deleted id={}", taskEvent.taskId)
                }
            } catch (ex: Exception) {
                logger.error("[TaskEventConsumerHandler] Failed to handle event type={}", envelope.eventType, ex)
            }
        }
    }

    private fun deserialize(eventType: String, payload: String): TaskEvent {
        return when (eventType) {
            "task.created" -> objectMapper.readValue(payload, TaskEvent.Created::class.java)
            "task.updated" -> objectMapper.readValue(payload, TaskEvent.Updated::class.java)
            "task.deleted" -> objectMapper.readValue(payload, TaskEvent.Deleted::class.java)
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
    }
}
