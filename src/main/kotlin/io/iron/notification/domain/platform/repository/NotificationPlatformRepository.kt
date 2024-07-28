package io.iron.notification.domain.platform.repository

import io.iron.notification.domain.platform.domain.NotificationPlatform

interface NotificationPlatformRepository {
    fun findByName(platformName: String): NotificationPlatform?
}