package com.example.demo.models.tasks

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostTaskDto(
    @field:NotBlank
    @field:Size(max = 255)
    val title: String,

    @field:Size(max = 1000)
    val description: String? = null,

    val status: TaskStatus = TaskStatus.OPEN,
)
