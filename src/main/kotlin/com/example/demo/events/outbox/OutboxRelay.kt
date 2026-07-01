package com.example.demo.events.outbox

import com.example.demo.events.core.EventEnvelope
import com.example.demo.events.core.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxRelay(
    private val outboxRepository: OutboxRepository,
    private val eventPublisher: EventPublisher,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${outbox.poll-interval:1000}")
    @Transactional
    fun relay() {
        val rows = outboxRepository.findByPublishedAtIsNullOrderByCreatedAtAsc()
        if (rows.isEmpty()) return

        logger.info("[OutboxRelay] Relaying {} outbox event(s)", rows.size)

        for (row in rows) {
            try {
                val envelope = EventEnvelope(
                    eventId = row.id.toString(),
                    eventType = row.eventType,
                    correlationId = row.correlationId,
                    payload = row.payload,
                )
                eventPublisher.publish(envelope)
                outboxRepository.delete(row)
                logger.info("[OutboxRelay] Published and deleted outbox event id={} type={}", row.id, row.eventType)
            } catch (ex: Exception) {
                logger.error("[OutboxRelay] Failed to relay event id={} type={}", row.id, row.eventType, ex)
            }
        }
    }
}
