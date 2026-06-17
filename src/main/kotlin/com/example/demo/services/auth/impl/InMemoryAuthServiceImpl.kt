package com.example.demo.services.auth.impl

import com.example.demo.models.auth.AuthResponse
import com.example.demo.models.auth.LoginRequest
import com.example.demo.models.auth.RegisterRequest
import com.example.demo.services.auth.AuthService
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class InMemoryAuthServiceImpl : AuthService {

    private data class InMemoryUser(
        val id: Long,
        val username: String,
        val password: String,
    )

    private val users = ConcurrentHashMap<Long, InMemoryUser>()
    private val usernameToId = ConcurrentHashMap<String, Long>()
    private val idCounter = AtomicLong(0)

    private val secret = "in-memory-secret-key-that-is-long-enough-for-hmac-sha-256!!"
    private val expirationMs = 3600000L
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    override fun register(request: RegisterRequest) {
        if (usernameToId.containsKey(request.username)) {
            throw IllegalArgumentException("Username already taken")
        }
        val id = idCounter.incrementAndGet()
        val user = InMemoryUser(
            id = id,
            username = request.username,
            password = request.password,
        )
        users[id] = user
        usernameToId[request.username] = id
    }

    override fun login(request: LoginRequest): AuthResponse {
        val userId = usernameToId[request.username]
            ?: throw IllegalArgumentException("Invalid username or password")
        val user = users[userId]!!

        if (user.password != request.password) {
            throw IllegalArgumentException("Invalid username or password")
        }

        val now = Date()
        val expiry = Date(now.time + expirationMs)
        val token = Jwts.builder()
            .subject(userId.toString())
            .claim("username", user.username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()

        return AuthResponse(token = token, userId = user.id, username = user.username)
    }
}
