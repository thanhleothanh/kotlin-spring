package com.example.demo.repositories.tasks

import com.example.demo.entities.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository : JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity>