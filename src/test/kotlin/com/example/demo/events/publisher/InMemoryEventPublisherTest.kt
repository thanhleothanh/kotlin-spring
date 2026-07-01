package com.example.demo.events.publisher

import com.example.demo.events.core.EventEnvelope
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InMemoryEventPublisherTest {

    @Test
    fun `publish pushes envelope to channel`() {
        val channel = InMemoryEventChannel()
        val publisher = InMemoryEventPublisher(channel)

        val envelope = EventEnvelope(
            eventType = "test.event",
            correlationId = "corr-1",
            payload = "{}",
        )
        publisher.publish(envelope)

        val result = channel.poll()
        assertEquals(envelope, result)
    }

    @Test
    fun `publish multiple events`() {
        val channel = InMemoryEventChannel()
        val publisher = InMemoryEventPublisher(channel)

        repeat(5) { i ->
            publisher.publish(
                EventEnvelope(
                    eventId = i.toString(),
                    eventType = "test.event",
                    correlationId = "corr-$i",
                    payload = "{}",
                )
            )
        }

        for (i in 0 until 5) {
            val result = channel.poll()
            assertNotNull(result)
            assertEquals(i.toString(), result.eventId)
        }
    }
}
