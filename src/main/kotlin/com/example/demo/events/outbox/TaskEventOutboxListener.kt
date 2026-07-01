package com.example.demo.events.outbox

import com.example.demo.events.task.TaskEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TaskEventOutboxListener(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener
    @Transactional
    fun onTaskEvent(event: TaskEvent) {
        val payload = objectMapper.writeValueAsString(event)

        val entity = OutboxEntity(
            eventType = event.eventType,
            correlationId = java.util.UUID.randomUUID().toString(),
            payload = payload,
        )
        outboxRepository.save(entity)
        logger.info("[TaskEventOutboxListener] Saved outbox event type={} id={}", event.eventType, entity.id)
    }
}
