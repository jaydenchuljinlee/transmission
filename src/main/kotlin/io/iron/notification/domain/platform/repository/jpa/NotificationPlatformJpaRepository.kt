package io.iron.notification.domain.platform.repository.jpa

import io.iron.notification.domain.platform.domain.NotificationPlatform
import io.iron.notification.domain.platform.repository.NotificationPlatformRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationPlatformJpaRepository : JpaRepository<NotificationPlatform, Long>,
    NotificationPlatformRepository {
}