package com.example.demo.models.tasks

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

enum class TaskStatus(val status: Int) {
    OPEN(0),
    DONE(1),
    DISCARDED(2);

    sealed interface CompletedAtEffect {
        data object Set : CompletedAtEffect
        data object Clear : CompletedAtEffect
    }

    fun onTransitionFrom(previous: TaskStatus): CompletedAtEffect? {
        if (this == DONE && previous != DONE) return CompletedAtEffect.Set
        if (this != DONE && previous == DONE) return CompletedAtEffect.Clear
        return null
    }

    @Converter
    class TaskStatusConverter : AttributeConverter<TaskStatus, Int> {
        override fun convertToDatabaseColumn(attribute: TaskStatus?): Int? = attribute?.status

        override fun convertToEntityAttribute(dbData: Int?): TaskStatus? =
            dbData?.let { value -> TaskStatus.entries.first { it.status == value } }
    }
}