package io.iron.notification.domain.notification.service

import io.iron.notification.domain.notification.domain.NotificationDeliveryLog
import io.iron.notification.domain.notification.repository.jpa.NotificationDeliveryLogJpaRepository
import org.springframework.stereotype.Service

@Service
class NotificationDeliveryLogService(
    private val failedNotificationRepository: NotificationDeliveryLogJpaRepository
) {
    fun save(entity: NotificationDeliveryLog) {
        failedNotificationRepository.save(entity)
    }
}