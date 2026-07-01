package com.example.demo.events.outbox

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OutboxRepository : JpaRepository<OutboxEntity, UUID> {
    fun findByPublishedAtIsNullOrderByCreatedAtAsc(): List<OutboxEntity>
}
