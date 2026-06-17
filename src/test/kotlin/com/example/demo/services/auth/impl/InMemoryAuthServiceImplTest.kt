package com.example.demo.services.auth.impl

import com.example.demo.services.auth.AuthService
import com.example.demo.services.auth.AuthServiceTest

class InMemoryAuthServiceImplTest: AuthServiceTest() {

    override fun createAuthService(): AuthService {
        return InMemoryAuthServiceImpl()
    }
}