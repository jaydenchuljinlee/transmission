package io.iron.notification.domain.notification.repository

import io.iron.notification.domain.notification.UserNotificationPlatform

interface UserNotificationPlatformRepository {
    fun findAllByUserId(userId: Long): List<UserNotificationPlatform>
    fun findAllByPlatformId(platformId: Long): List<UserNotificationPlatform>
}