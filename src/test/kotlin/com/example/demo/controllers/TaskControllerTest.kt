package com.example.demo.controllers

import com.example.demo.configurations.ControllerIntegrationTest
import com.example.demo.factories.tasks.TaskTestFactory.postTaskDto1
import com.example.demo.factories.tasks.TaskTestFactory.postTaskDto2
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskStatus
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@ControllerIntegrationTest
class TaskControllerTest {

    companion object {
        private val AUTH_REQUEST = mapOf("username" to "testUser1", "password" to "password123")
    }

    @LocalServerPort
    private var port: Int = 0

    private lateinit var authToken: String

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        authToken = registerAndLogin()
    }

    private fun registerAndLogin(): String {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .post("/api/auth/register")

        val response = RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .post("/api/auth/login")
            .then()
            .statusCode(200)
            .extract()

        return response.path<String>("token")
    }

    private fun authenticated(): RequestSpecification {
        return RestAssured.given()
            .header("Authorization", "Bearer $authToken")
    }

    @Test
    fun `getTasks returns empty list when no tasks exist`() {
        authenticated()
            .`when`()
            .get("/api/tasks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `postTask creates and returns the task with 201`() {
        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to "Task One", "description" to "First task"))
            .`when`()
            .post("/api/tasks")
            .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("title", equalTo("Task One"))
            .body("description", equalTo("First task"))
            .body("status", equalTo(TaskStatus.OPEN.name))
            .body("completedAt", nullValue())
    }

    @Test
    fun `postTask with blank title returns 400`() {
        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to ""))
            .`when`()
            .post("/api/tasks")
            .then()
            .statusCode(400)
    }

    @Test
    fun `getTasks returns all posted tasks`() {
        extractCreatedTaskId(postTaskDto1)
        extractCreatedTaskId(postTaskDto2)

        authenticated()
            .`when`()
            .get("/api/tasks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("title", hasItems("Task One", "Task Two"))
    }

    @Test
    fun `patchTask updates title`() {
        val id = extractCreatedTaskId(postTaskDto1)

        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to "Updated Title"))
            .`when`()
            .patch("/api/tasks/{id}", id)
            .then()
            .statusCode(200)
            .body("title", equalTo("Updated Title"))
            .body("description", equalTo(postTaskDto1.description))
    }

    @Test
    fun `patchTask status transition to DONE sets completedAt`() {
        val id = extractCreatedTaskId(postTaskDto1)

        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("status" to "DONE"))
            .`when`()
            .patch("/api/tasks/{id}", id)
            .then()
            .statusCode(200)
            .body("status", equalTo(TaskStatus.DONE.name))
            .body("completedAt", notNullValue())
    }

    @Test
    fun `patchTask status transition away from DONE clears completedAt`() {
        val id = extractCreatedTaskId(postTaskDto1)

        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("status" to "DONE"))
            .patch("/api/tasks/{id}", id)

        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("status" to "OPEN"))
            .`when`()
            .patch("/api/tasks/{id}", id)
            .then()
            .statusCode(200)
            .body("status", equalTo(TaskStatus.OPEN.name))
            .body("completedAt", nullValue())
    }

    @Test
    fun `patchTask with non-existent id returns 404`() {
        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to "Updated"))
            .`when`()
            .patch("/api/tasks/{id}", 99999)
            .then()
            .statusCode(404)
    }

    @Test
    fun `deleteTask removes existing task`() {
        val id = extractCreatedTaskId(postTaskDto1)

        authenticated()
            .`when`()
            .delete("/api/tasks/{id}", id)
            .then()
            .statusCode(204)

        authenticated()
            .`when`()
            .get("/api/tasks")
            .then()
            .body("size()", equalTo(0))
    }

    @Test
    fun `deleteTask with non-existent id returns 404`() {
        authenticated()
            .`when`()
            .delete("/api/tasks/{id}", 99999)
            .then()
            .statusCode(404)
    }

    @Test
    fun `getTaskStats returns counts and recent tasks`() {
        val task1Id = extractCreatedTaskId(postTaskDto1)
        val task2Id = extractCreatedTaskId(postTaskDto2)

        authenticated()
            .contentType(ContentType.JSON)
            .body(mapOf("status" to TaskStatus.DONE))
            .patch("/api/tasks/{id}", task1Id)

        authenticated()
            .`when`()
            .get("/api/tasks/stats")
            .then()
            .statusCode(200)
            .body("totalTasks", equalTo(2))
            .body("openTasks", equalTo(1))
            .body("doneTasks", equalTo(1))
            .body("discardedTasks", equalTo(0))
            .body("recentTasks.size()", equalTo(2))
    }

    @Test
    fun `getTaskStats returns zeros when no tasks exist`() {
        authenticated()
            .`when`()
            .get("/api/tasks/stats")
            .then()
            .statusCode(200)
            .body("totalTasks", equalTo(0))
            .body("openTasks", equalTo(0))
            .body("doneTasks", equalTo(0))
            .body("discardedTasks", equalTo(0))
            .body("recentTasks.size()", equalTo(0))
    }

    @Test
    fun `unauthenticated request returns 401`() {
        RestAssured.given()
            .`when`()
            .get("/api/tasks")
            .then()
            .statusCode(401)
    }

    private fun extractCreatedTaskId(postTaskDto: PostTaskDto): Long {
        return authenticated()
            .contentType(ContentType.JSON)
            .body(postTaskDto)
            .post("/api/tasks")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")
            .toLong()
    }
}
