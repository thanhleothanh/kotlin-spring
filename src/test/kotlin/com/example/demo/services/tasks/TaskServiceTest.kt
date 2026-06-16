package com.example.demo.services.tasks

import com.example.demo.factories.tasks.TaskTestDataFactory.patchTaskTitle
import com.example.demo.factories.tasks.TaskTestDataFactory.patchTaskStatusToDone
import com.example.demo.factories.tasks.TaskTestDataFactory.patchTaskStatusToOpen
import com.example.demo.factories.tasks.TaskTestDataFactory.postTaskDto1
import com.example.demo.factories.tasks.TaskTestDataFactory.postTaskDto2
import com.example.demo.models.tasks.TaskStatus
import jakarta.persistence.EntityNotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class TaskServiceTest {

    protected abstract val taskService: TaskService

    @Test
    fun `postTask creates and returns task with generated id`() {
        val result = taskService.postTask(postTaskDto1)

        assertNotNull(result.id)
        assertEquals(postTaskDto1.title, result.title)
        assertEquals(postTaskDto1.description, result.description)
        assertEquals(postTaskDto1.status, result.status)
        assertNull(result.completedAt)
    }

    @Test
    fun `getTasks returns all posted tasks`() {
        taskService.postTask(postTaskDto1)
        taskService.postTask(postTaskDto2)
        val tasks = taskService.getTasks()

        assertEquals(2, tasks.size)
    }

    @Test
    fun `patchTask updates title and description`() {
        val created = taskService.postTask(postTaskDto1)
        val patched = taskService.patchTask(created.id, patchTaskTitle)

        assertEquals(patchTaskTitle.title.get(), patched.title)
        assertEquals(created.description, patched.description)
        assertEquals(created.status, patched.status)
    }

    @Test
    fun `patchTask status transition to DONE sets completedAt`() {
        val created = taskService.postTask(postTaskDto1)
        val patched = taskService.patchTask(created.id, patchTaskStatusToDone)

        assertEquals(postTaskDto1.title, patched.title)
        assertEquals(postTaskDto1.description, patched.description)
        assertEquals(patchTaskStatusToDone.status.get(), patched.status)
        assertNotNull(patched.completedAt)
    }

    @Test
    fun `patchTask status transition away from DONE clears completedAt`() {
        val created = taskService.postTask(postTaskDto1)
        taskService.patchTask(created.id, patchTaskStatusToDone)
        val reopened = taskService.patchTask(created.id, patchTaskStatusToOpen)

        assertEquals(TaskStatus.OPEN, reopened.status)
        assertNull(reopened.completedAt)
    }

    @Test
    fun `patchTask with non-existent id throws EntityNotFoundException`() {
        val ex = assertThrows<EntityNotFoundException> {
            taskService.patchTask(99999L, patchTaskTitle)
        }

        assertEquals("Task not found with id 99999", ex.message)
    }

    @Test
    fun `deleteTask removes existing task`() {
        val created = taskService.postTask(postTaskDto1)

        assertEquals(1, taskService.getTasks().size)

        taskService.deleteTask(created.id)

        assertEquals(emptyList<Any>(), taskService.getTasks())
    }

    @Test
    fun `deleteTask with non-existent id throws EntityNotFoundException`() {
        val ex = assertThrows<EntityNotFoundException> {
            taskService.deleteTask(99999L)
        }

        assertEquals("Task not found with id 99999", ex.message)
    }
}
