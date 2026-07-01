package com.example.demo.events.core

interface EventConsumer {
    fun subscribe(handler: (EventEnvelope) -> Unit)
}
