package io.iron.notification.domain.user.repository

import io.iron.notification.domain.user.domain.UserSlackTokenInfo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserCacheRepository: CrudRepository<UserSlackTokenInfo, Long> {
}