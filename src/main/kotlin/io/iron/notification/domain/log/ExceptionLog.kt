package io.iron.notification.domain.log

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime


@Document(collection = "exceptionLogs")
data class ExceptionLog(
    @Id
    val id: String? = null,
    val timestamp: LocalDateTime,
    val message: String?,
    val stackTrace: String?
)