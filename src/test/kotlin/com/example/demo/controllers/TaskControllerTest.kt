package com.example.demo.controllers

import com.example.demo.configurations.ControllerIntegrationTest
import com.example.demo.models.tasks.TaskStatus
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.server.LocalServerPort

@ControllerIntegrationTest
class TaskControllerTest {

    @LocalServerPort
    private var port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }

    @Test
    fun `getTasks returns empty list when no tasks exist`() {
        RestAssured.given()
            .`when`()
            .get("/api/tasks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(0))
    }

    @Test
    fun `postTask creates and returns the task with 201`() {
        RestAssured.given()
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
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to ""))
            .`when`()
            .post("/api/tasks")
            .then()
            .statusCode(400)
    }

    @Test
    fun `getTasks returns all posted tasks`() {
        val task1 = mapOf("title" to "Task One", "description" to "First task")
        val task2 = mapOf("title" to "Task Two", "description" to "Second task")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(task1)
            .post("/api/tasks")

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(task2)
            .post("/api/tasks")

        RestAssured.given()
            .`when`()
            .get("/api/tasks")
            .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("title", hasItems("Task One", "Task Two"))
    }

    @Test
    fun `patchTask updates title`() {
        val id = extractCreatedTaskId()

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to "Updated Title"))
            .`when`()
            .patch("/api/tasks/{id}", id)
            .then()
            .statusCode(200)
            .body("title", equalTo("Updated Title"))
            .body("description", equalTo("First task"))
    }

    @Test
    fun `patchTask status transition to DONE sets completedAt`() {
        val id = extractCreatedTaskId()

        RestAssured.given()
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
        val id = extractCreatedTaskId()

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(mapOf("status" to "DONE"))
            .patch("/api/tasks/{id}", id)

        RestAssured.given()
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
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to "Updated"))
            .`when`()
            .patch("/api/tasks/{id}", 99999)
            .then()
            .statusCode(404)
    }

    @Test
    fun `deleteTask removes existing task`() {
        val id = extractCreatedTaskId()

        RestAssured.given()
            .`when`()
            .delete("/api/tasks/{id}", id)
            .then()
            .statusCode(204)

        RestAssured.given()
            .`when`()
            .get("/api/tasks")
            .then()
            .body("size()", equalTo(0))
    }

    @Test
    fun `deleteTask with non-existent id returns 404`() {
        RestAssured.given()
            .`when`()
            .delete("/api/tasks/{id}", 99999)
            .then()
            .statusCode(404)
    }

    private fun extractCreatedTaskId(): Long {
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .body(mapOf("title" to "Task One", "description" to "First task"))
            .post("/api/tasks")
            .then()
            .statusCode(201)
            .extract()
            .path<Int>("id")
            .toLong()
    }
}
