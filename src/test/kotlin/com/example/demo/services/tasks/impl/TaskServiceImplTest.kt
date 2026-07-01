package com.example.demo.services.tasks.impl

import com.example.demo.configurations.DatabaseIntegrationTest
import com.example.demo.services.auth.AuthService
import com.example.demo.services.tasks.TaskService
import com.example.demo.services.tasks.TaskServiceTest
import org.springframework.beans.factory.annotation.Autowired

@DatabaseIntegrationTest
class TaskServiceImplTest : TaskServiceTest() {

    @Autowired
    private lateinit var injectedTaskService: TaskService

    @Autowired
    private lateinit var injectedAuthService: AuthService

    override fun createTaskService(): TaskService = injectedTaskService

    override fun createAuthService(): AuthService = injectedAuthService
}
