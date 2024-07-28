package io.iron.notification.domain.user.repository

import io.iron.notification.domain.user.domain.UserInfo

interface UserInfoRepository {
    fun findByNickname(nickname: String): UserInfo?
}