package io.iron.notification.global.handler

import io.iron.notification.global.domain.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest(ex.message ?: "Invalid request"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse> {
        val errors = ex.bindingResult.allErrors.joinToString(", ") { error ->
            val fieldError = error as FieldError
            "${fieldError.field}: ${error.defaultMessage}"
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.badRequest(errors))
    }
}