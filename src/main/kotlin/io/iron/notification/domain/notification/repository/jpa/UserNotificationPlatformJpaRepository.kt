package io.iron.notification.domain.notification.repository.jpa

import io.iron.notification.domain.notification.UserNotificationPlatform
import io.iron.notification.domain.notification.repository.UserNotificationPlatformRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserNotificationPlatformJpaRepository: JpaRepository<UserNotificationPlatform, Long>, UserNotificationPlatformRepository {
}