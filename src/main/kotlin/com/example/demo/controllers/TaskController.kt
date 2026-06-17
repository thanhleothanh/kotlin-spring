package com.example.demo.controllers

import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskDto
import com.example.demo.security.UserPrincipal
import com.example.demo.services.tasks.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTasks(@AuthenticationPrincipal principal: UserPrincipal): List<TaskDto> {
        return taskService.getTasks(principal.id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postTask(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: PostTaskDto,
    ): TaskDto {
        return taskService.postTask(principal.id, request)
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun patchTask(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
        @Valid @RequestBody request: PatchTaskDto,
    ): TaskDto {
        return taskService.patchTask(principal.id, id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable id: Long,
    ) {
        taskService.deleteTask(principal.id, id)
    }
}
