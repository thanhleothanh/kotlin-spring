package com.example.demo.events.consumer

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusProcessorClient
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode
import com.example.demo.events.core.EventConsumer
import com.example.demo.events.core.EventEnvelope
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory

class ServiceBusEventConsumer(
    private val builder: ServiceBusClientBuilder,
    private val queueName: String,
    private val objectMapper: ObjectMapper,
) : EventConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val handlers = mutableListOf<(EventEnvelope) -> Unit>()
    private lateinit var processorClient: ServiceBusProcessorClient

    override fun subscribe(handler: (EventEnvelope) -> Unit) {
        handlers.add(handler)
    }

    @PostConstruct
    fun start() {
        processorClient = builder
            .processor()
            .queueName(queueName)
            .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
            .processMessage { context ->
                val json = context.message.body.toString()
                onMessage(json)
                context.complete()
            }
            .processError { context ->
                logger.error("ServiceBus error on queue {}", context.entityPath, context.exception)
            }
            .buildProcessorClient()

        processorClient.start()
        logger.info("ServiceBus processor started for queue={}", queueName)
    }

    @PreDestroy
    fun stop() {
        processorClient.close()
        logger.info("ServiceBus processor stopped")
    }

    fun onMessage(json: String) {
        try {
            val envelope = objectMapper.readValue(json, EventEnvelope::class.java)
            logger.info("[ServiceBusEventConsumer] Consumed event type={} id={}", envelope.eventType, envelope.eventId)
            for (handler in handlers) {
                try {
                    handler(envelope)
                } catch (ex: Exception) {
                    logger.error("[ServiceBusEventConsumer] Handler failed for event type={} id={}", envelope.eventType, envelope.eventId, ex)
                }
            }
        } catch (ex: Exception) {
            logger.error("[ServiceBusEventConsumer] Failed to deserialize event", ex)
        }
    }
}
