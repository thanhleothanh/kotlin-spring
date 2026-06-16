package com.example.demo.models.tasks

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.openapitools.jackson.nullable.JsonNullable

data class PatchTaskDto(
    @field:NotBlank
    @field:Size(max = 255)
    val title: JsonNullable<String> = JsonNullable.undefined(),

    @field:Size(max = 1000)
    val description: JsonNullable<String?> = JsonNullable.undefined(),

    @field:NotNull
    val status: JsonNullable<TaskStatus> = JsonNullable.undefined(),
)
