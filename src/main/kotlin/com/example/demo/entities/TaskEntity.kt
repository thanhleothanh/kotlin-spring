package com.example.demo.entities

import com.example.demo.models.tasks.TaskStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "tasks")
class TaskEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt: Instant = Instant.now(),

    @Column(name = "title")
    var title: String,

    @Column(name = "status")
    @Convert(converter = TaskStatus.TaskStatusConverter::class)
    var status: TaskStatus,

    @Column(name = "description")
    var description: String?,

    @Column(name = "completed_at")
    var completedAt: Instant? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,
)