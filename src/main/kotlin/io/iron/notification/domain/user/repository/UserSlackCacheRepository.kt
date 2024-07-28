package io.iron.notification.domain.user.repository

import io.iron.notification.domain.user.domain.UserSlackInfo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSlackCacheRepository: CrudRepository<UserSlackInfo, Long> {
}