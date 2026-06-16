package com.example.demo.services.tasks.impl

import com.example.demo.configurations.DatabaseIntegrationTest
import com.example.demo.repositories.tasks.TaskRepository
import com.example.demo.services.tasks.TaskService
import com.example.demo.services.tasks.TaskServiceTest
import org.springframework.beans.factory.annotation.Autowired

@DatabaseIntegrationTest
class TaskServiceImplTest : TaskServiceTest() {

    @Autowired
    lateinit var taskRepository: TaskRepository

    override fun createTaskService(): TaskService {
        return TaskServiceImpl(taskRepository)
    }
}
