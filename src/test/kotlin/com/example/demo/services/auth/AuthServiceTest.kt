package com.example.demo.services.auth

import com.example.demo.factories.auth.UserTestFactory.loginRequest1
import com.example.demo.factories.auth.UserTestFactory.registerRequest1
import com.example.demo.models.auth.LoginRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

abstract class AuthServiceTest {

    protected abstract fun createAuthService(): AuthService

    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = createAuthService()
    }

    @Test
    fun `login with valid credentials returns auth response`() {
        authService.register(registerRequest1)

        val response = authService.login(loginRequest1)

        assertNotNull(response.token)
        assertEquals(registerRequest1.username, response.username)
    }

    @Test
    fun `register with duplicate username throws`() {
        authService.register(registerRequest1)

        val ex = assertThrows<IllegalArgumentException> {
            authService.register(registerRequest1)
        }

        assertEquals("Username already taken", ex.message)
    }

    @Test
    fun `login with wrong password throws`() {
        authService.register(registerRequest1)

        val ex = assertThrows<IllegalArgumentException> {
            authService.login(LoginRequest(username = registerRequest1.username, password = "wrongPassword"))
        }

        assertEquals("Invalid username or password", ex.message)
    }

    @Test
    fun `login with non-existent user throws`() {
        val ex = assertThrows<IllegalArgumentException> {
            authService.login(loginRequest1)
        }

        assertEquals("Invalid username or password", ex.message)
    }
}
