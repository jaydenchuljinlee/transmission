package io.iron.notification.domain.notification.repository

import io.iron.notification.domain.notification.NotificationPlatform

interface NotificationPlatformRepository {
    fun findByName(platformName: String): NotificationPlatform?
}