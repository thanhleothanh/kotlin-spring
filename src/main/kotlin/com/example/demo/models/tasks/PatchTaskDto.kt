package com.example.demo.models.tasks

import jakarta.validation.constraints.Size
import org.openapitools.jackson.nullable.JsonNullable

data class PatchTaskDto(
    @field:Size(max = 255)
    val title: JsonNullable<String> = JsonNullable.undefined(),

    @field:Size(max = 1000)
    val description: JsonNullable<String?> = JsonNullable.undefined(),

    val status: JsonNullable<TaskStatus> = JsonNullable.undefined(),
)
