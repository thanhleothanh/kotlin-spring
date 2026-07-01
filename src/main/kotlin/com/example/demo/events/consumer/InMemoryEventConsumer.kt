package com.example.demo.events.consumer

import com.example.demo.events.core.EventConsumer
import com.example.demo.events.core.EventEnvelope
import com.example.demo.events.publisher.InMemoryEventChannel
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class InMemoryEventConsumer(
    private val channel: InMemoryEventChannel,
) : EventConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val handlers = mutableListOf<(EventEnvelope) -> Unit>()

    override fun subscribe(handler: (EventEnvelope) -> Unit) {
        handlers.add(handler)
    }

    @Scheduled(fixedDelay = 100)
    fun pollQueue() {
        val envelope = channel.poll() ?: return
        logger.info("[InMemoryEventConsumer] Consumed event type={} id={}", envelope.eventType, envelope.eventId)
        handlers.forEach { handler ->
            try {
                handler(envelope)
            } catch (ex: Exception) {
                logger.error("[InMemoryEventConsumer] Handler failed for event type={} id={}", envelope.eventType, envelope.eventId, ex)
            }
        }
    }
}
