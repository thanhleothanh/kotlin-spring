package com.example.demo.services.tasks.impl

import com.example.demo.configurations.DatabaseIntegrationTest
import com.example.demo.repositories.UserRepository
import com.example.demo.repositories.tasks.TaskRepository
import com.example.demo.security.JwtTokenProvider
import com.example.demo.services.auth.AuthService
import com.example.demo.services.auth.impl.AuthServiceImpl
import com.example.demo.services.tasks.TaskService
import com.example.demo.services.tasks.TaskServiceTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

@DatabaseIntegrationTest
class TaskServiceImplTest : TaskServiceTest() {

    @Autowired
    private lateinit var taskRepository: TaskRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    override fun createTaskService(): TaskService {
        return TaskServiceImpl(taskRepository, userRepository)
    }

    override fun createAuthService(): AuthService {
        return AuthServiceImpl(userRepository, passwordEncoder, jwtTokenProvider)
    }

}
