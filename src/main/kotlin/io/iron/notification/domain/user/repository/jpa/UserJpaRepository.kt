package io.iron.notification.domain.user.repository.jpa

import io.iron.notification.domain.user.UserInfo
import io.iron.notification.domain.user.repository.UserInfoRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserInfo, Long>, UserInfoRepository