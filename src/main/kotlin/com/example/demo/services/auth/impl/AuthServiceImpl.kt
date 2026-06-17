package com.example.demo.services.auth.impl

import com.example.demo.entities.UserEntity
import com.example.demo.models.auth.AuthResponse
import com.example.demo.models.auth.LoginRequest
import com.example.demo.models.auth.RegisterRequest
import com.example.demo.repositories.UserRepository
import com.example.demo.security.JwtTokenProvider
import com.example.demo.services.auth.AuthService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) : AuthService {

    override fun register(request: RegisterRequest) {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already taken")
        }

        val entity = UserEntity(
            username = request.username,
            password = passwordEncoder.encode(request.password)
        )
        userRepository.save(entity)
    }

    override fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw IllegalArgumentException("Invalid username or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid username or password")
        }

        val token = jwtTokenProvider.generateToken(
            userId = requireNotNull(user.id),
            username = user.username,
        )
        return AuthResponse(token = token, userId = user.id!!, username = user.username)
    }
}
