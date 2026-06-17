package com.example.demo.models.auth

data class AuthResponse(
    val token: String,
    val userId: Long,
    val username: String,
)
