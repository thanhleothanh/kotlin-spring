package com.example.demo.services.tasks.impl

import com.example.demo.configurations.DatabaseIntegrationTest
import com.example.demo.services.tasks.TaskServiceTest
import com.example.demo.services.tasks.TaskService
import org.springframework.beans.factory.annotation.Autowired

@DatabaseIntegrationTest
class TaskServiceImplTest : TaskServiceTest() {

    @Autowired
    override lateinit var taskService: TaskService
}
