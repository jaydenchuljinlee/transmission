package io.iron.notification.domain.notification.repository

import io.iron.notification.domain.notification.domain.NotificationGroup

interface NotificationGroupRepository {
    fun findByName(groupName: String): NotificationGroup?
}