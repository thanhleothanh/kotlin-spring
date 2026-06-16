package com.example.demo.models.common

data class ErrorResponse(
    val status: Int,
    val message: String? = null,
    val details: Map<String, String?>? = null,
)