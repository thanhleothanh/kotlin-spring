package com.example.demo.events.publisher

import com.azure.messaging.servicebus.ServiceBusMessage
import com.azure.messaging.servicebus.ServiceBusSenderClient
import com.example.demo.events.core.EventEnvelope
import com.example.demo.events.core.EventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

class ServiceBusEventPublisher(
    private val senderClient: ServiceBusSenderClient,
    private val objectMapper: ObjectMapper,
) : EventPublisher {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(envelope: EventEnvelope) {
        try {
            val json = objectMapper.writeValueAsString(envelope)
            val message = ServiceBusMessage(json)
            message.applicationProperties.put("eventType", envelope.eventType)
            message.correlationId = envelope.correlationId
            message.messageId = envelope.eventId

            senderClient.sendMessage(message)
            logger.info("[ServiceBusEventPublisher] Published event type={} id={}", envelope.eventType, envelope.eventId)
        } catch (ex: Exception) {
            logger.error("[ServiceBusEventPublisher] Failed to publish event type={} id={}", envelope.eventType, envelope.eventId, ex)
            throw ex
        }
    }
}
