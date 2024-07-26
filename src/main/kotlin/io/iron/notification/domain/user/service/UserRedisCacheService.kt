package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserSlackTokenInfo
import io.iron.notification.domain.user.repository.UserCacheRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserRedisCacheService(
    private val cacheRepository: UserCacheRepository
): UserCacheService {
    override fun save(entity: UserSlackTokenInfo) {
        cacheRepository.save(entity)
    }

    override fun get(id: Long): Optional<UserSlackTokenInfo> {
        return cacheRepository.findById(id)
    }
}