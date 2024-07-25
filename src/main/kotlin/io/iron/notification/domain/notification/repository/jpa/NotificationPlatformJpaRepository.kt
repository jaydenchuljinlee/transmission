package io.iron.notification.domain.notification.repository.jpa

import io.iron.notification.domain.notification.NotificationPlatform
import io.iron.notification.domain.notification.repository.NotificationPlatformRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationPlatformJpaRepository : JpaRepository<NotificationPlatform, Long>, NotificationPlatformRepository {
}