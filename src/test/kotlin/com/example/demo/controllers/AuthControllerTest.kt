package com.example.demo.controllers

import com.example.demo.configurations.ControllerIntegrationTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.web.server.LocalServerPort


@ControllerIntegrationTest
class AuthControllerTest {

    companion object {
        private val AUTH_REQUEST = mapOf("username" to "testUser1", "password" to "password123")
    }

    @LocalServerPort
    private var port: Int = 0

    @Value("\${server.servlet.context-path}")
    private lateinit var contextPath: String

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
        RestAssured.basePath = contextPath
    }

    @Test
    fun `register creates user`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .`when`()
            .post("/api/auth/register")
            .then()
            .statusCode(201)
    }

    @Test
    fun `register with duplicate username returns 400`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .post("/api/auth/register")
            .then()
            .statusCode(201)

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .`when`()
            .post("/api/auth/register")
            .then()
            .statusCode(400)
    }

    @Test
    fun `login with valid credentials returns token`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .post("/api/auth/register")
            .then()
            .statusCode(201)

        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .`when`()
            .post("/api/auth/login")
            .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("userId", notNullValue())
            .body("username", equalTo(AUTH_REQUEST["username"]))
    }

    @Test
    fun `login with wrong password returns 400`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .post("/api/auth/register")
            .then()
            .statusCode(201)

        val wrongLoginInfo = mapOf("username" to AUTH_REQUEST["username"], "password" to "wrongpassword")
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(wrongLoginInfo)
            .`when`()
            .post("/api/auth/login")
            .then()
            .statusCode(400)
    }

    @Test
    fun `login with non-existent user returns 400`() {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body(AUTH_REQUEST)
            .`when`()
            .post("/api/auth/login")
            .then()
            .statusCode(400)
    }
}
