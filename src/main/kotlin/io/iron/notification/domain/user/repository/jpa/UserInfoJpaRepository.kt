package io.iron.notification.domain.user.repository.jpa

import io.iron.notification.domain.user.domain.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoJpaRepository : JpaRepository<UserInfo, Long> {
    fun findByNickname(nickname: String): UserInfo?

    fun findByEmail(email: String): UserInfo?

    fun findByEmailOrNickname(email: String, nickname: String): UserInfo?
}