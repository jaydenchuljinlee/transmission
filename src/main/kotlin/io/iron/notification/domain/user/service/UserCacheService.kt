package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserSlackTokenInfo
import java.util.*

interface UserCacheService {
    fun save(entity: UserSlackTokenInfo)
    fun get(id: Long): Optional<UserSlackTokenInfo>
}