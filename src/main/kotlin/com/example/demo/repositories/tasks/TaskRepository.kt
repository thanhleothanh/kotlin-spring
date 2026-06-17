package com.example.demo.repositories.tasks

import com.example.demo.entities.TaskEntity
import com.example.demo.models.tasks.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {
    fun findAllByUserId(userId: Long): List<TaskEntity>
    fun findByIdAndUserId(id: Long, userId: Long): TaskEntity?
    fun countByUserId(userId: Long): Long
    fun countByUserIdAndStatus(userId: Long, status: TaskStatus): Long
    fun findTop10ByUserIdOrderByCreatedAtDesc(userId: Long): List<TaskEntity>
}