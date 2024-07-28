package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.repository.UserSlackCacheRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserSlackCacheService(
    private val slackCacheRepository: UserSlackCacheRepository
) {
    fun save(entity: UserSlackInfo) {
        slackCacheRepository.save(entity)
    }

    fun saveAll(entities: List<UserSlackInfo>) {
        slackCacheRepository.saveAll(entities)
    }

    fun get(id: Long): Optional<UserSlackInfo> {
        return slackCacheRepository.findById(id)
    }
}