package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserSlackInfo
import java.util.*

interface UserCacheService {
    fun save(entity: UserSlackInfo)

    fun saveAll(entities: List<UserSlackInfo>)

    fun get(id: Long): Optional<UserSlackInfo>
}