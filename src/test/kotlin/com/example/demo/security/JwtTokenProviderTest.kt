package com.example.demo.security

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private val provider = JwtTokenProvider("nscN1dNJ5Qvy8V/Stw7dxvjPWM3Iey2feUoo2RnWakQ=", 86400000)

    @Test
    fun `generateToken creates valid token`() {
        val token = provider.generateToken(1L, "testuser")
        assertNotNull(token)
        assertTrue(token.isNotBlank())
    }

    @Test
    fun `validateToken returns true for valid token`() {
        val token = provider.generateToken(1L, "testuser")
        assertTrue(provider.validateToken(token))
    }

    @Test
    fun `validateToken returns false for invalid token`() {
        assertFalse(provider.validateToken("invalid.token.here"))
    }

    @Test
    fun `getUserId extracts correct id from token`() {
        val token = provider.generateToken(42L, "testuser")
        assertEquals(42L, provider.getUserId(token))
    }

    @Test
    fun `getUsername extracts correct username from token`() {
        val token = provider.generateToken(1L, "alice")
        assertEquals("alice", provider.getUsername(token))
    }
}
