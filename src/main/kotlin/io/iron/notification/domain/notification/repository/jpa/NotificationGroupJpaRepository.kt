package io.iron.notification.domain.notification.repository.jpa

import io.iron.notification.domain.notification.NotificationGroup
import io.iron.notification.domain.notification.repository.NotificationGroupRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationGroupJpaRepository: JpaRepository<NotificationGroup, Long>, NotificationGroupRepository {
}