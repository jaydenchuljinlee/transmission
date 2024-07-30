package io.iron.notification.domain.notification.repository.jpa

import io.iron.notification.domain.notification.domain.NotificationDeliveryLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationDeliveryLogJpaRepository: JpaRepository<NotificationDeliveryLog, Long> {
}