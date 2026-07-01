package com.example.demo.events.consumer

import com.example.demo.events.core.EventEnvelope
import com.example.demo.events.publisher.InMemoryEventChannel
import com.example.demo.events.publisher.InMemoryEventPublisher
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryEventConsumerTest {

    @Test
    fun `consumer dispatches event to subscriber`() {
        val channel = InMemoryEventChannel()
        val publisher = InMemoryEventPublisher(channel)
        val consumer = InMemoryEventConsumer(channel)

        val received = mutableListOf<EventEnvelope>()
        consumer.subscribe { received.add(it) }

        val envelope = EventEnvelope(
            eventType = "test.event",
            correlationId = "corr-1",
            payload = "{}",
        )
        publisher.publish(envelope)
        consumer.pollQueue()

        assertEquals(1, received.size)
        assertEquals(envelope, received[0])
    }

    @Test
    fun `consumer dispatches to multiple subscribers`() {
        val channel = InMemoryEventChannel()
        val publisher = InMemoryEventPublisher(channel)
        val consumer = InMemoryEventConsumer(channel)

        val received1 = mutableListOf<EventEnvelope>()
        val received2 = mutableListOf<EventEnvelope>()
        consumer.subscribe { received1.add(it) }
        consumer.subscribe { received2.add(it) }

        publisher.publish(
            EventEnvelope(
                eventType = "test.event",
                correlationId = "corr-1",
                payload = "{}",
            )
        )
        consumer.pollQueue()

        assertEquals(1, received1.size)
        assertEquals(1, received2.size)
    }

    @Test
    fun `consumer does not dispatch when queue is empty`() {
        val channel = InMemoryEventChannel()
        val consumer = InMemoryEventConsumer(channel)

        var called = false
        consumer.subscribe { called = true }

        consumer.pollQueue()

        assertTrue(!called)
    }

    @Test
    fun `consumer handles multiple events in sequence`() {
        val channel = InMemoryEventChannel()
        val publisher = InMemoryEventPublisher(channel)
        val consumer = InMemoryEventConsumer(channel)

        val received = mutableListOf<EventEnvelope>()
        consumer.subscribe { received.add(it) }

        repeat(3) { i ->
            publisher.publish(
                EventEnvelope(
                    eventId = i.toString(),
                    eventType = "test.event",
                    correlationId = "corr-$i",
                    payload = "{}",
                )
            )
        }

        repeat(3) {
            consumer.pollQueue()
        }

        assertEquals(3, received.size)
        assertEquals("0", received[0].eventId)
        assertEquals("1", received[1].eventId)
        assertEquals("2", received[2].eventId)
    }

    @Test
    fun `consumer subscriber error does not affect subsequent dispatches`() {
        val channel = InMemoryEventChannel()
        val publisher = InMemoryEventPublisher(channel)
        val consumer = InMemoryEventConsumer(channel)

        val received = mutableListOf<EventEnvelope>()
        consumer.subscribe { throw RuntimeException("handler error") }
        consumer.subscribe { received.add(it) }

        publisher.publish(
            EventEnvelope(
                eventType = "test.event",
                correlationId = "corr-1",
                payload = "{}",
            )
        )

        consumer.pollQueue()

        assertEquals(1, received.size)
    }
}
