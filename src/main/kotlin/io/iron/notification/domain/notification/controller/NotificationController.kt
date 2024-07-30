package io.iron.notification.domain.notification.controller

import io.iron.notification.domain.notification.controller.request.NotificationRequest
import io.iron.notification.domain.notification.service.NotificationService
import io.iron.notification.global.domain.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/alerts")
class NotificationController(
    private val notificationService: NotificationService
) {
    @PostMapping
    fun sendAlert(
        @Valid @RequestBody request: NotificationRequest,
    ): ApiResponse {
        val targetCount = notificationService.sendExternalAlert(request.target, request.severity, request.message)
        return ApiResponse.ok(message = "알림 메시지 전송 성공", data = targetCount)
    }
}