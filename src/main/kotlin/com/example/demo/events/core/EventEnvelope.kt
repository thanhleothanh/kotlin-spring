package com.example.demo.events.core

import java.time.Instant
import java.util.UUID

data class EventEnvelope(
    val eventId: String = UUID.randomUUID().toString(),
    val eventType: String,
    val correlationId: String = UUID.randomUUID().toString(),
    val timestamp: Instant = Instant.now(),
    val payload: String,
)
