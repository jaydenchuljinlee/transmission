package io.iron.notification.domain.user.service

import io.iron.notification.domain.platform.domain.UserChannel
import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.platform.repository.jpa.UserChannelJpaRepository
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.springframework.stereotype.Service

@Service
class UserInfoService(
    private val userInfoRepository: UserInfoJpaRepository,

) {
    fun getUsers(): List<UserInfo> {
        return userInfoRepository.findAll()
    }
}