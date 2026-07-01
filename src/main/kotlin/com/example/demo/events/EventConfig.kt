package com.example.demo.events

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusSenderClient
import com.example.demo.events.consumer.InMemoryEventConsumer
import com.example.demo.events.consumer.ServiceBusEventConsumer
import com.example.demo.events.core.EventConsumer
import com.example.demo.events.core.EventPublisher
import com.example.demo.events.publisher.InMemoryEventChannel
import com.example.demo.events.publisher.InMemoryEventPublisher
import com.example.demo.events.publisher.ServiceBusEventPublisher
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventConfig {

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "in-memory", matchIfMissing = true)
    fun inMemoryEventChannel(): InMemoryEventChannel = InMemoryEventChannel()

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "in-memory", matchIfMissing = true)
    fun inMemoryEventPublisher(channel: InMemoryEventChannel): EventPublisher = InMemoryEventPublisher(channel)

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "in-memory", matchIfMissing = true)
    fun inMemoryEventConsumer(channel: InMemoryEventChannel): EventConsumer = InMemoryEventConsumer(channel)

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "asb")
    fun serviceBusClientBuilder(
        @Value("\${ASB_CONNECTION_STRING:}") connectionString: String,
    ): ServiceBusClientBuilder {
        require(connectionString.isNotBlank()) { "ASB_CONNECTION_STRING env var must be set when events.mode=asb" }
        return ServiceBusClientBuilder()
            .connectionString(connectionString)
    }

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "asb")
    fun serviceBusSenderClient(
        builder: ServiceBusClientBuilder,
        @Value("\${app.servicebus.queue-name}") queueName: String,
    ): ServiceBusSenderClient {
        return builder
            .sender()
            .queueName(queueName)
            .buildClient()
    }

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "asb")
    fun serviceBusEventPublisher(
        senderClient: ServiceBusSenderClient,
        objectMapper: ObjectMapper,
    ): EventPublisher = ServiceBusEventPublisher(senderClient, objectMapper)

    @Bean
    @ConditionalOnProperty(name = ["events.mode"], havingValue = "asb")
    fun serviceBusEventConsumer(
        builder: ServiceBusClientBuilder,
        @Value("\${app.servicebus.queue-name}") queueName: String,
        objectMapper: ObjectMapper,
    ): EventConsumer = ServiceBusEventConsumer(builder, queueName, objectMapper)
}
