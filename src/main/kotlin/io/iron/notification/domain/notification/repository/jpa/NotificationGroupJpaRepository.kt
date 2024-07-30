package io.iron.notification.domain.notification.repository.jpa

import io.iron.notification.domain.notification.domain.NotificationGroup
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationGroupJpaRepository: JpaRepository<NotificationGroup, Long> {
    fun findByName(groupName: String): NotificationGroup?
}