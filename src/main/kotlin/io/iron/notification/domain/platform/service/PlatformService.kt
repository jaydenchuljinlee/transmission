package io.iron.notification.domain.platform.service

import io.iron.notification.domain.platform.domain.UserChannel
import io.iron.notification.domain.platform.repository.jpa.UserChannelJpaRepository
import org.springframework.stereotype.Service

@Service
class PlatformService(
    private val userChannelJpaRepository: UserChannelJpaRepository
) {
    fun findActiveChannelsBy(userId: Long): List<UserChannel> {
        return userChannelJpaRepository.findActiveChannelsByUserId(userId)
    }
}