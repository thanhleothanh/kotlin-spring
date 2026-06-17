package com.example.demo.services.tasks.impl

import com.example.demo.services.auth.AuthService
import com.example.demo.services.auth.impl.InMemoryAuthServiceImpl
import com.example.demo.services.tasks.TaskService
import com.example.demo.services.tasks.TaskServiceTest

class InMemoryTaskServiceImplTest : TaskServiceTest() {

    override fun createTaskService(): TaskService {
        return InMemoryTaskServiceImpl()
    }

    override fun createAuthService(): AuthService {
        return InMemoryAuthServiceImpl()
    }
}
