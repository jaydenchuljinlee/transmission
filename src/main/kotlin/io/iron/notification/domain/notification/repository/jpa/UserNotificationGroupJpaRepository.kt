package io.iron.notification.domain.notification.repository.jpa

import io.iron.notification.domain.notification.domain.UserNotificationGroup
import io.iron.notification.domain.notification.repository.UserNotificationGroupRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserNotificationGroupJpaRepository: JpaRepository<UserNotificationGroup, Long>, UserNotificationGroupRepository {
}