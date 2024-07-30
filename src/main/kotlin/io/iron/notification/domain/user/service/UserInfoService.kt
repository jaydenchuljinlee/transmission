package io.iron.notification.domain.user.service

import io.iron.notification.domain.platform.domain.UserChannel
import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.platform.repository.jpa.UserChannelJpaRepository
import io.iron.notification.domain.user.exception.UserInfoDuplicatedException
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.springframework.stereotype.Service

@Service
class UserInfoService(
    private val userInfoRepository: UserInfoJpaRepository,

) {
    fun create(nickname: String, email: String): UserInfo {
        val existingUser = userInfoRepository.findByEmailOrNickname(email, nickname)

        if (existingUser == null) {
            val userInfo = UserInfo(nickname = nickname, email = email)
            return userInfoRepository.save(userInfo)
        }

        if (existingUser.email == email) {
            throw UserInfoDuplicatedException("이미 존재하는 이메일입니다.")
        } else {
            throw UserInfoDuplicatedException("이미 존재하는 이름입니다.")
        }
    }

    fun getUsers(): List<UserInfo> {
        return userInfoRepository.findAll()
    }
}