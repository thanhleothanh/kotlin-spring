package com.example.demo.services.tasks

import com.example.demo.factories.tasks.TaskTestFactory.patchTaskStatusToDone
import com.example.demo.factories.tasks.TaskTestFactory.patchTaskStatusToOpen
import com.example.demo.factories.tasks.TaskTestFactory.patchTaskTitle
import com.example.demo.factories.tasks.TaskTestFactory.postTaskDto1
import com.example.demo.factories.tasks.TaskTestFactory.postTaskDto2
import com.example.demo.models.auth.LoginRequest
import com.example.demo.models.auth.RegisterRequest
import com.example.demo.models.tasks.TaskStatus
import com.example.demo.services.auth.AuthService
import jakarta.persistence.EntityNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class TaskServiceTest {

    protected abstract fun createTaskService(): TaskService
    protected abstract fun createAuthService(): AuthService

    private lateinit var taskService: TaskService
    private lateinit var authService: AuthService

    private var testUserId: Long = 1

    @BeforeEach
    fun setUp() {
        taskService = createTaskService()
        authService = createAuthService()

        val registerRequest = RegisterRequest(username = "testuser", password = "password")
        authService.register(registerRequest)
        val loginRequest = LoginRequest(username = "testuser", password = "password")
        val authResponse = authService.login(loginRequest)
        testUserId = requireNotNull(authResponse.userId)
    }

    @Test
    fun `postTask creates and returns task with generated id`() {
        val result = taskService.postTask(testUserId, postTaskDto1)

        assertNotNull(result.id)
        assertEquals(postTaskDto1.title, result.title)
        assertEquals(postTaskDto1.description, result.description)
        assertEquals(postTaskDto1.status, result.status)
        assertNull(result.completedAt)
    }

    @Test
    fun `getTasks returns all posted tasks for user`() {
        taskService.postTask(testUserId, postTaskDto1)
        taskService.postTask(testUserId, postTaskDto2)
        val tasks = taskService.getTasks(testUserId)

        assertEquals(2, tasks.size)
    }

    @Test
    fun `getTasks does not return tasks from other users`() {
        taskService.postTask(testUserId, postTaskDto1)
        val otherTasks = taskService.getTasks(999L)

        assertEquals(0, otherTasks.size)
    }

    @Test
    fun `patchTask updates title and description`() {
        val created = taskService.postTask(testUserId, postTaskDto1)
        val patched = taskService.patchTask(testUserId, created.id, patchTaskTitle)

        assertEquals(patchTaskTitle.title.get(), patched.title)
        assertEquals(created.description, patched.description)
        assertEquals(created.status, patched.status)
    }

    @Test
    fun `patchTask status transition to DONE sets completedAt`() {
        val created = taskService.postTask(testUserId, postTaskDto1)
        val patched = taskService.patchTask(testUserId, created.id, patchTaskStatusToDone)

        assertEquals(postTaskDto1.title, patched.title)
        assertEquals(postTaskDto1.description, patched.description)
        assertEquals(patchTaskStatusToDone.status.get(), patched.status)
        assertNotNull(patched.completedAt)
    }

    @Test
    fun `patchTask status transition away from DONE clears completedAt`() {
        val created = taskService.postTask(testUserId, postTaskDto1)
        taskService.patchTask(testUserId, created.id, patchTaskStatusToDone)
        val reopened = taskService.patchTask(testUserId, created.id, patchTaskStatusToOpen)

        assertEquals(TaskStatus.OPEN, reopened.status)
        assertNull(reopened.completedAt)
    }

    @Test
    fun `patchTask with non-existent id throws EntityNotFoundException`() {
        val ex = assertThrows<EntityNotFoundException> {
            taskService.patchTask(testUserId, 99999L, patchTaskTitle)
        }

        assertEquals("Task not found with id 99999", ex.message)
    }

    @Test
    fun `deleteTask removes existing task`() {
        val created = taskService.postTask(testUserId, postTaskDto1)

        assertEquals(1, taskService.getTasks(testUserId).size)

        taskService.deleteTask(testUserId, created.id)

        assertEquals(emptyList<Any>(), taskService.getTasks(testUserId))
    }

    @Test
    fun `deleteTask with non-existent id throws EntityNotFoundException`() {
        val ex = assertThrows<EntityNotFoundException> {
            taskService.deleteTask(testUserId, 99999L)
        }

        assertEquals("Task not found with id 99999", ex.message)
    }

    @Test
    fun `getTaskStats returns zero counts when user has no tasks`() = runBlocking {
        val stats = taskService.getTaskStats(testUserId)

        assertEquals(0L, stats.totalTasks)
        assertEquals(0L, stats.openTasks)
        assertEquals(0L, stats.doneTasks)
        assertEquals(0L, stats.discardedTasks)
        assertTrue(stats.recentTasks.isEmpty())
    }

    @Test
    fun `getTaskStats recentTasks are ordered by createdAt descending`() = runBlocking {
        val task1 = taskService.postTask(testUserId, postTaskDto1)
        val task2 = taskService.postTask(testUserId, postTaskDto2)
        val task3 = taskService.postTask(testUserId, postTaskDto1)

        val stats = taskService.getTaskStats(testUserId)

        assertEquals(3, stats.recentTasks.size)
        assertEquals(task3.id, stats.recentTasks[0].id)
        assertEquals(task2.id, stats.recentTasks[1].id)
        assertEquals(task1.id, stats.recentTasks[2].id)
    }

    @Test
    fun `getTaskStats returns correct counts after posting tasks with various statuses`() = runBlocking {
        val task1 = taskService.postTask(testUserId, postTaskDto1)
        val task2 = taskService.postTask(testUserId, postTaskDto2)
        taskService.patchTask(testUserId, task1.id, patchTaskStatusToDone)
        taskService.patchTask(testUserId, task2.id, patchTaskStatusToOpen)

        val stats = taskService.getTaskStats(testUserId)

        assertEquals(2L, stats.totalTasks)
        assertEquals(1L, stats.openTasks)
        assertEquals(1L, stats.doneTasks)
        assertEquals(0L, stats.discardedTasks)
    }
}
