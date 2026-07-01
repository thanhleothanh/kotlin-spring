package com.example.demo.events.core

import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventEnvelopeTest {

    @Test
    fun `envelope generates eventId on creation`() {
        val envelope = EventEnvelope(
            eventType = "test.event",
            correlationId = "corr-1",
            payload = "{}",
        )
        assertNotNull(envelope.eventId)
        assertNotNull(UUID.fromString(envelope.eventId))
    }

    @Test
    fun `envelope generates correlationId on creation`() {
        val envelope = EventEnvelope(
            eventType = "test.event",
            payload = "{}",
        )
        assertNotNull(envelope.correlationId)
        assertNotNull(UUID.fromString(envelope.correlationId))
    }

    @Test
    fun `envelope sets timestamp on creation`() {
        val envelope = EventEnvelope(
            eventType = "test.event",
            correlationId = "corr-1",
            payload = "{}",
        )
        assertNotNull(envelope.timestamp)
    }

    @Test
    fun `envelope with explicit eventId uses provided value`() {
        val envelope = EventEnvelope(
            eventId = "explicit-id",
            eventType = "test.event",
            correlationId = "corr-1",
            payload = "{}",
        )
        assertTrue(envelope.eventId == "explicit-id")
    }
}
