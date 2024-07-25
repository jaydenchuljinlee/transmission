package io.iron.notification.domain.user.repository

import io.iron.notification.domain.user.UserInfo
import io.iron.notification.global.repository.BaseRepository
import java.util.Optional

interface UserInfoRepository {
    fun findByNickname(nickname: String): UserInfo?
    fun findAllByNicknameIn(nicknames: List<String>): List<UserInfo>
}