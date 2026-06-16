package com.example.demo.models.common

import org.springframework.http.HttpStatus

data class ResponseWrapper<T>(
    val code: Int,
    val data: T? = null
) {
    companion object {
        fun <T> of(code: Int, data: T): ResponseWrapper<T> {
            return ResponseWrapper(code, data)
        }

        fun <T> ok(data: T): ResponseWrapper<T> {
            return ResponseWrapper(HttpStatus.OK.value(), data)
        }

        fun <T> notFound(): ResponseWrapper<T> {
            return ResponseWrapper(HttpStatus.NOT_FOUND.value())
        }

        fun <T> error(code: Int): ResponseWrapper<T> {
            return ResponseWrapper(code)
        }
    }
}