package com.example.demo.mappers

import com.example.demo.factories.auth.UserTestFactory.userEntity1
import com.example.demo.factories.tasks.TaskTestFactory.postTaskDto1
import com.example.demo.factories.tasks.TaskTestFactory.taskEntityStatusDone
import com.example.demo.factories.tasks.TaskTestFactory.taskEntityStatusOpen
import com.example.demo.factories.tasks.TaskTestFactory.taskEntityWithNoID
import com.example.demo.models.tasks.TaskStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TaskMapperTest {

    @Test
    fun `toDto maps all fields correctly`() {
        val dto = TaskMapper.toDto(taskEntityStatusDone)

        assertEquals(2L, dto.id)
        assertEquals("Entity Two", dto.title)
        assertEquals(TaskStatus.DONE, dto.status)
        assertNull(dto.description)
        assertNotNull(dto.completedAt)
    }

    @Test
    fun `toDto maps nullable fields to null`() {
        val dto = TaskMapper.toDto(taskEntityStatusOpen)

        assertEquals(1L, dto.id)
        assertEquals("Task One", dto.title)
        assertEquals("First task", dto.description)
        assertNull(dto.completedAt)
        assertEquals(TaskStatus.OPEN, dto.status)
    }

    @Test
    fun `toDto throws when entity id is null`() {
        val ex = assertThrows<IllegalArgumentException> {
            TaskMapper.toDto(taskEntityWithNoID)
        }

        assertEquals("Entity ID must not be null when mapping to a response DTO", ex.message)
    }

    @Test
    fun `toEntity maps PostTaskDto correctly`() {
        val entity = TaskMapper.toEntity(postTaskDto1, userEntity1)

        assertNull(entity.id)
        assertEquals(postTaskDto1.title, entity.title)
        assertEquals(postTaskDto1.description, entity.description)
        assertEquals(postTaskDto1.status, entity.status)
        assertNull(entity.completedAt)
        assertNotNull(entity.createdAt)
        assertNotNull(entity.updatedAt)
    }
}
