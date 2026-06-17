package com.example.demo.services.auth.impl

import com.example.demo.configurations.DatabaseIntegrationTest
import com.example.demo.repositories.UserRepository
import com.example.demo.security.JwtTokenProvider
import com.example.demo.services.auth.AuthService
import com.example.demo.services.auth.AuthServiceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@DatabaseIntegrationTest
class AuthServiceImplTest: AuthServiceTest() {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    override fun createAuthService(): AuthService {
        return AuthServiceImpl(userRepository, passwordEncoder, jwtTokenProvider)
    }
}