package io.iron.notification.global.domain

import org.springframework.http.HttpStatus

data class ApiResponse(
    val status: Int,
    val message: String,
    val data: Any?
) {
    companion object {
        fun ok(data: Any? = null, message: String = ""): ApiResponse {
            return ApiResponse(
                status = HttpStatus.OK.value(),
                message = message,
                data = data
            )
        }

        fun badRequest(message: String, status: HttpStatus = HttpStatus.BAD_REQUEST): ApiResponse {
            return ApiResponse(
                status = status.value(),
                message = message,
                data = null
            )
        }

        fun internalError(message: String, status: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR): ApiResponse {
            return ApiResponse(
                status = status.value(),
                message = message,
                data = null
            )
        }
    }
}