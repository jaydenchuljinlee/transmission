package io.iron.notification.domain.notification.repository

import io.iron.notification.domain.notification.domain.NotificationDeliveryLog
import io.iron.notification.domain.notification.domain.enumeration.NotificationStatus

interface NotificationDeliveryLogRepository {
    fun findByStatus(status: NotificationStatus): List<NotificationDeliveryLog>
}