package com.example.demo.factories.tasks

import com.example.demo.entities.TaskEntity
import com.example.demo.factories.auth.UserTestFactory
import com.example.demo.models.tasks.PatchTaskDto
import com.example.demo.models.tasks.PostTaskDto
import com.example.demo.models.tasks.TaskStatus
import org.openapitools.jackson.nullable.JsonNullable
import java.time.Instant

object TaskTestFactory {

    val postTaskDto1 = PostTaskDto(title = "Task One", description = "First task")
    val postTaskDto2 = PostTaskDto(title = "Task Two", description = "Second task")

    val patchTaskTitle = PatchTaskDto(title = JsonNullable.of("Updated"))
    val patchTaskStatusToDone = PatchTaskDto(status = JsonNullable.of(TaskStatus.DONE))
    val patchTaskStatusToOpen = PatchTaskDto(status = JsonNullable.of(TaskStatus.OPEN))

    val taskEntityWithNoID = TaskEntity(
        title = "Task with no ID",
        description = "First task",
        status = TaskStatus.OPEN,
        user = UserTestFactory.userEntity1,
    )
    val taskEntityStatusOpen = TaskEntity(
        id = 1L,
        title = "Task One",
        description = "First task",
        status = TaskStatus.OPEN,
        user = UserTestFactory.userEntity1,
    )
    val taskEntityStatusDone = TaskEntity(
        id = 2L,
        title = "Entity Two",
        description = null,
        status = TaskStatus.DONE,
        completedAt = Instant.now(), user = UserTestFactory.userEntity1,
    )
}
