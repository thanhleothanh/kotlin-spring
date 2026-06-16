package com.example.demo.entities

import com.example.demo.models.tasks.TaskStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "tasks")
class TaskEntity(
    @Column(name = "title")
    var title: String,

    @Column(name = "status")
    @Convert(converter = TaskStatus.TaskStatusConverter::class)
    var status: TaskStatus,

    @Column(name = "description")
    var description: String?,

    @Column(name = "completed_at")
    var completedAt: Instant?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @Column(name = "updated_at")
    @UpdateTimestamp
    val updatedAt: Instant = Instant.now()
}