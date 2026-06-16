package com.example.demo.exceptions

import com.example.demo.models.common.ErrorResponse
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(EntityNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: EntityNotFoundException): ErrorResponse {
        return ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.message)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(ex: MethodArgumentNotValidException): ErrorResponse {
        val details = ex.bindingResult.fieldErrors
            .associateBy({ it.field }, { it.defaultMessage })
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.message, details)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMalformedJson(ex: HttpMessageNotReadableException, request: HttpServletRequest): ErrorResponse {
        return ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.message)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleAll(ex: Exception): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message)
    }

}
