package com.example.demo.controllers

import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.services.tasks.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTasks(): List<TaskDto> {
        return taskService.getTasks()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postTask(@Valid @RequestBody request: PostTaskDto): TaskDto {
        return taskService.postTask(request)
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun patchTask(@PathVariable id: Long, @Valid @RequestBody request: PatchTaskDto): TaskDto {
        return taskService.patchTask(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long) {
        taskService.deleteTask(id)
    }
}
