package com.example.demo.events.publisher

import com.example.demo.events.core.EventEnvelope
import com.example.demo.events.core.EventPublisher
import org.slf4j.LoggerFactory

class InMemoryEventPublisher(
    private val channel: InMemoryEventChannel,
) : EventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(envelope: EventEnvelope) {
        channel.push(envelope)
        logger.info("[InMemoryEventPublisher] Published event type={} id={}", envelope.eventType, envelope.eventId)
    }
}
