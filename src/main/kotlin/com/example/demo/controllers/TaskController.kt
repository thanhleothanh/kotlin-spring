package com.example.demo.controllers

import com.example.demo.models.common.ResponseWrapper
import com.example.demo.models.tasks.CreateTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.services.tasks.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    fun getTasks(): ResponseEntity<ResponseWrapper<List<TaskDto>>> {
        val result = taskService.getTasks()
        return ResponseEntity.ok(ResponseWrapper.ok(result))
    }

    @PostMapping
    fun postTask(@RequestBody request: CreateTaskDto): ResponseEntity<ResponseWrapper<*>> {
        taskService.createTask(request)
        return ResponseEntity.ok(ResponseWrapper.ok(null))
    }
}