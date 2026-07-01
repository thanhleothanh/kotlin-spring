package com.example.demo.events.core

interface EventPublisher {
    fun publish(envelope: EventEnvelope)
}
