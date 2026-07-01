package com.example.demo.events.publisher

import com.example.demo.events.core.EventEnvelope
import org.springframework.stereotype.Component
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

@Component
class InMemoryEventChannel {
    private val queue: BlockingQueue<EventEnvelope> = LinkedBlockingQueue()

    fun push(envelope: EventEnvelope) {
        queue.put(envelope)
    }

    fun poll(): EventEnvelope? = queue.poll()
}
