package io.iron.notification.domain.log.service

import io.iron.notification.domain.log.ExceptionLog
import io.iron.notification.domain.log.repository.ExceptionLogRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ExceptionLogService(
    private val exceptionLogRepository: ExceptionLogRepository
) {
    fun logException(exception: Exception) {
        val logEntry = ExceptionLog(
            timestamp = LocalDateTime.now(),
            message = exception.message,
            stackTrace = exception.stackTrace.joinToString("\n")
        )
        exceptionLogRepository.save(logEntry)
    }

    fun getAllLogs(): List<ExceptionLog> {
        return exceptionLogRepository.findAll()
    }
}