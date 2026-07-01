package com.example.demo.events.publisher

import com.example.demo.events.core.EventEnvelope
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class InMemoryEventChannelTest {

    private val channel = InMemoryEventChannel()

    @Test
    fun `poll returns null when queue is empty`() {
        assertNull(channel.poll())
    }

    @Test
    fun `push then poll returns the envelope`() {
        val envelope = EventEnvelope(
            eventType = "test.event",
            correlationId = "corr-1",
            payload = "{}",
        )
        channel.push(envelope)
        val result = channel.poll()
        assertEquals(envelope, result)
    }

    @Test
    fun `poll respects FIFO order`() {
        val envelope1 = EventEnvelope(
            eventId = "1",
            eventType = "first",
            correlationId = "corr-1",
            payload = "{}",
        )
        val envelope2 = EventEnvelope(
            eventId = "2",
            eventType = "second",
            correlationId = "corr-2",
            payload = "{}",
        )
        channel.push(envelope1)
        channel.push(envelope2)

        assertEquals(envelope1, channel.poll())
        assertEquals(envelope2, channel.poll())
        assertNull(channel.poll())
    }
}
