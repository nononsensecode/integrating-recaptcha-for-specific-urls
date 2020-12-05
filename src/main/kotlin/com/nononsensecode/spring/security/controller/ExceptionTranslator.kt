package com.nononsensecode.spring.security.controller

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {  }

@RestControllerAdvice
class ExceptionTranslator {

    @ExceptionHandler(IllegalAccessException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalAccessException(e: IllegalAccessException): ApiError {
        logger.error { e }
        return ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e.message ?: "Illegal access to the resource")
    }
}

data class ApiError(
    val status: HttpStatus,
    val message: String
)