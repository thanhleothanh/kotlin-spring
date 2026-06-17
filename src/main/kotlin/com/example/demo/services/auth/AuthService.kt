package com.example.demo.services.auth

import com.example.demo.models.auth.AuthResponse
import com.example.demo.models.auth.LoginRequest
import com.example.demo.models.auth.RegisterRequest

interface AuthService {
    fun register(request: RegisterRequest)
    fun login(request: LoginRequest): AuthResponse
}
