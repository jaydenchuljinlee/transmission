package io.iron.notification.domain.log.repository

import io.iron.notification.domain.log.ExceptionLog
import org.springframework.data.mongodb.repository.MongoRepository

interface ExceptionLogRepository: MongoRepository<ExceptionLog, String> {
}