package io.iron.notification.domain.log.controller

import io.iron.notification.domain.log.ExceptionLog
import io.iron.notification.domain.log.service.ExceptionLogProducer
import io.iron.notification.domain.log.service.ExceptionLogService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/logs")
class ExceptionLogController(
        private val exceptionLogService: ExceptionLogService,
        private val exceptionLogProducer: ExceptionLogProducer,
) {
    @PostMapping("/log")
    fun logException() {
        try {
            throw RuntimeException("Test Exception")
        } catch (e: Exception) {
            exceptionLogService.logException(e)
            exceptionLogProducer.sendLogToQueue(e.message!!)
        }
    }

    @GetMapping
    fun getLogs(): List<ExceptionLog> {
        return exceptionLogService.getAllLogs()
    }
}